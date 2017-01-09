package com.fr.bi.web.dezi.phantom;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.web.dezi.phantom.utils.PhantomServerUtils;
import com.fr.general.IOUtils;
import com.fr.general.RunTimeErorException;
import com.fr.json.JSONObject;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;
import com.fr.web.ResourceHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * phantom server
 * Created by AstronautOO7 on 2016/12/21.
 */
public class PhantomServer {

    private static final String IP = "127.0.0.1";
    private static final int PORT = 8090;

    private static Map<String, String> osMap = new HashMap<String, String>();
    private static String PhantomEnv = FRContext.getCurrentEnv().getPath() + BIBaseConstant.PHANTOM.PHANTOM_PATH;
    private static String PhantomLib = PhantomEnv + "/lib";
    private static String PhantomJequry = PhantomEnv + "/jquery";
    private static String PhantomResources = FRContext.getCurrentEnv().getPath() + "/classes/com/fr/bi/web/js/third";
    private static String PhantomCss = FRContext.getCurrentEnv().getPath() + "/classes/com/fr/bi/web/css/base/third/leaflet.css";

    private static Map<Locale, String> localeJsMap = new HashMap<Locale, String>();

    private static final int BUFF_LENGTH = 8192;

    static {
        osMap.put("linux32", "/phantomjs");
        osMap.put("linux64", "/phantomjs");
        osMap.put("macOS", "/phantomjs");
        osMap.put("windows", "/phantomjs.exe");
        osMap.put("unix32", "/phantomjs");
        osMap.put("unix64", "/phantomjs");
    }

    //resources needed in the phantom server
    private static final String[][] SCRIPT_SOURCES = {
            new String[]{PhantomResources + "/d3.js","d3.js"},
            new String[]{PhantomResources + "/vancharts-all.js","vancharts-all.js"},
            new String[]{PhantomResources + "/leaflet.js", "leaflet.js"}
    };

    //format js of chart
    private static String FORMAT_JS;

    private static final String[] FORMAT_BASE = {
            PhantomJequry + "/jquery.js",
            PhantomJequry + "/jquery.base.js"
    };

    private static final String[] DATA = {
            PhantomEnv + "/jquery.date.js"
    };
    private static final String[] FORMAT = {
            PhantomEnv + "/jquery.format.js"
    };

    //日期格式，国际化相关，需要每次注入新的
    private static String DATA_JS;

    //创建格式文件
    static {
        FORMAT_JS = createFormat();
    }

    private static String createFormat(){
        String formatBase = new StringBuffer().append(IOUtils.concatFiles(FORMAT_BASE, '\n')).append("\n").toString();
        String localeJs = fetchJS(FRContext.getLocale());
        String formatJs = new StringBuffer().append(IOUtils.concatFiles(FORMAT, '\n')).append("\n").toString();
        DATA_JS = new StringBuffer().append(IOUtils.concatFiles(DATA, '\n')).append("\n").toString();
        return new StringBuffer().append(formatBase).append(localeJs).append(DATA_JS).append(formatJs).toString();
    }

    public static String fetchJS(Locale locale){
        String localeJS = localeJsMap.get(locale);
        if (StringUtils.isEmpty(localeJS)){
            try {
                localeJsMap.put(locale, ResourceHelper.fetchI18NJs(locale));
            } catch (Exception e) {
                return StringUtils.EMPTY;
            }
        }
        return localeJsMap.get(locale);
    }

    public static String leafletCss = injectLeafletCss(PhantomCss);
    private static String injectLeafletCss(String path) {
        try {
            InputStream input = new FileInputStream(path);
            String string = IOUtils.inputStream2String(input);
            string = string.replace('\uFEFF', ' ');
            string = string.replace('\r',' ');
            string = string.replace('\n', ' ');
            return string;
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
            return StringUtils.EMPTY;
        }

    }

    public void start() throws IOException {
        if (PhantomServerUtils.checkServer(IP, PORT)) {
            return;
        }

        getResources(PhantomLib, SCRIPT_SOURCES);

        String exe = PhantomServerUtils.getExe(PhantomEnv);

        ArrayList<String> commands = new ArrayList<String>();
        commands.add(exe);
        commands.add(PhantomEnv + "/webserver.js");

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        Process process = processBuilder.start();

        final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        final String readLine = bufferedReader.readLine();
        if(readLine == null && !readLine.contains("ready")) {
            process.destroy();
            throw new RunTimeErorException("Error, PhantomJs can't start!");
        }

        injectAllCss();
    }

    private void injectAllCss () {
        try{
            JSONObject json = JSONObject.create();
            json.put("css", leafletCss);
            String cssCmd = json.toString();
            String res = PhantomServerUtils.postMessage(IP, PORT, cssCmd);
            if(PhantomServerUtils.isServerInjectSuccess(res)) {
                BILoggerFactory.getLogger().info("Success to inject css.");
            } else {
                BILoggerFactory.getLogger().info("Fail to inject css.");
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
    }

    private static void getResources (String libDir, String[][] libFiles) throws FileNotFoundException {
        //delete old lib with dependence resources
        File serverLib = new File(libDir);
        if(serverLib.exists() && serverLib.isDirectory()) {
            deleteDir(serverLib);
        }

        serverLib.mkdirs();

        //build new phantom server lib
        for (int i = 0; i < libFiles.length; i ++) {
            InputStream in = new FileInputStream(libFiles[i][0]);
            File file = new File(libDir + File.separator + libFiles[i][1]);
            try {
                inputStreamToFile(in, file);
            } catch (IOException e) {
                BILoggerFactory.getLogger().error(e.getMessage());
            }
        }

        try {
            //写入格式文件
            File formatFile = new File(libDir + File.separator + "format.js");
            ByteArrayInputStream is = new ByteArrayInputStream(FORMAT_JS.getBytes(StableUtils.RESOURCE_ENCODER));
            inputStreamToFile(is, formatFile);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
    }

    private static boolean deleteDir (File dir) {
        if(dir.isDirectory()) {
            String[] children = dir.list();
            //recursion to delete sub directory
            for (int i = 0; i < children.length; i++) {
                return deleteDir(new File(children[i]));
            }
        }
        return dir.delete();
    }

    private static void inputStreamToFile(InputStream in, File file) throws IOException {
        OutputStream outputStream = new FileOutputStream(file);
        int bytesRead = 0;
        byte[] buffer = new byte[BUFF_LENGTH];
        while ((bytesRead = in.read(buffer, 0, BUFF_LENGTH)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        in.close();
    }

}
