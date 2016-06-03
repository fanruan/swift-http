package com.fr.bi.web.dezi.services;

import com.fr.bi.base.BIUser;
import com.fr.bi.cal.stable.loader.CubeReadingTableIndexLoader;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * Created by Young's on 2016/1/28.
 */
public class BIGetPreviewTableDataAction extends AbstractBIDeziAction {
    @Override
    public String getCMD() {
        return "get_preview_table_data";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res,
                          String sessionID) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "table_id");
        CubeTableSource source = BIModuleUtils.getSourceByID(new BITableID(tableId), new BIUser(userId));
        JSONObject jo = source.createPreviewJSONFromCube(new ArrayList<String>(), CubeReadingTableIndexLoader.getInstance(userId));
        WebUtils.printAsJSON(res, jo);
    }
}