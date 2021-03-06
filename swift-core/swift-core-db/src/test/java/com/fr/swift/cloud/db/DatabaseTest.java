package com.fr.swift.cloud.db;

import com.fr.swift.cloud.config.entity.MetaDataColumnEntity;
import com.fr.swift.cloud.config.entity.SwiftMetaDataEntity;
import com.fr.swift.cloud.db.impl.SwiftDatabase;
import com.fr.swift.cloud.exception.meta.SwiftMetaDataException;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.source.SwiftMetaDataColumn;
import com.fr.swift.cloud.util.Util;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author anchore
 * @date 2018/4/4
 */
@Ignore
public class DatabaseTest {

    private Database db = SwiftDatabase.getInstance();

    @Test
    public void tableOpFlow() throws SQLException {
        List<SwiftMetaDataColumn> columnMetas = Arrays.<SwiftMetaDataColumn>asList(
                new MetaDataColumnEntity("A", Types.VARCHAR),
                new MetaDataColumnEntity("B", Types.DATE),
                new MetaDataColumnEntity("C", Types.INTEGER)
        );
        SourceKey tableKey = new SourceKey("a");
        SwiftMetaData meta = new SwiftMetaDataEntity("a", columnMetas);

        if (db.existsTable(tableKey)) {
            db.dropTable(tableKey);
        }

        db.createTable(tableKey, meta);
        assertTrue(db.existsTable(tableKey));

        Assert.assertEquals(tableKey, db.getTable(tableKey).getSourceKey());
        assertEquals(meta, db.getTable(tableKey).getMeta());

        // assertEquals(1, db.getAllTables().size());

        db.dropTable(tableKey);
        assertFalse(db.existsTable(tableKey));
    }

    private static void assertEquals(SwiftMetaData meta1, SwiftMetaData meta2) throws SwiftMetaDataException {
        if (meta1 == meta2) {
            return;
        }
        if (meta1 == null || meta2 == null) {
            fail();
        }
        if (meta1.getColumnCount() != meta2.getColumnCount()) {
            fail();
        }
        for (int i = 1; i <= meta1.getColumnCount(); i++) {
            SwiftMetaDataColumn columnMeta1 = meta1.getColumn(i);
            SwiftMetaDataColumn columnMeta2 = meta1.getColumn(i);
            assertEquals(columnMeta1, columnMeta2);
        }
    }

    public static void assertEquals(SwiftMetaDataColumn columnMeta1, SwiftMetaDataColumn columnMeta2) {
        if (!Util.equals(columnMeta1.getName(), columnMeta2.getName())) {
            fail();
        }
        if (!Util.equals(columnMeta1.getRemark(), columnMeta2.getRemark())) {
            fail();
        }
        if (columnMeta1.getType() != columnMeta2.getType()) {
            fail();
        }
        if (columnMeta1.getPrecision() != columnMeta2.getPrecision()) {
            fail();
        }
        if (columnMeta1.getScale() != columnMeta2.getScale()) {
            fail();
        }
        if (!Util.equals(columnMeta1.getColumnId(), columnMeta2.getColumnId())) {
            fail();
        }
    }
}