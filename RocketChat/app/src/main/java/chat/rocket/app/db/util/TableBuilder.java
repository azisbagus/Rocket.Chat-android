package chat.rocket.app.db.util;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import chat.rocket.app.BuildConfig;


public class TableBuilder {

    private static boolean PRINT_CREATION = BuildConfig.DEBUG;

    private static final String[] ACTIONS = {"NO ACTION", "RESTRICT", "SET NULL", "SET DEFAULT", "CASCADE"};

    private static final String COMMA = ",";
    // open bracket
    private static final String _OB_ = " ( ";

    // close bracket
    private static final String _CB_ = " ) ";

    public static final int NO_ACTION = 0;
    public static final int RESTRICT = 1;
    public static final int SET_NULL = 2;
    public static final int SET_DEFAULT = 3;
    public static final int CASCADE = 4;

    public static final String INTEGER = "INTEGER";
    public static final String REAL = "REAL";
    public static final String TEXT = "TEXT";
    public static final String BLOB = "BLOB";

    public static final String ON_CONFLICT_ROLLBACK = "ROLLBACK";
    public static final String ON_CONFLICT_ABORT = "ABORT";
    public static final String ON_CONFLICT_FAIL = "FAIL";
    public static final String ON_CONFLICT_IGNORE = "IGNORE";
    public static final String ON_CONFLICT_REPLACE = "REPLACE";

    private static final String _NOT_NULL_ = " NOT NULL ";
    private static final String CONSTRAINT_FK_ = "CONSTRAINT FK_";
    private static final String CONSTRAINT_UNIQUE_ = "CONSTRAINT UNIQUE_";
    private static final String _UNIQUE__ = " UNIQUE (";
    private static final String __ON_CONFLICT_ = ") ON CONFLICT ";

    private static final String _FOREIGN_KEY__ = " FOREIGN KEY (";
    private static final String __REFERENCES_ = ")  REFERENCES ";

    private static final String _AUTOINCREMENT_ = " AUTOINCREMENT ";

    private static final String _ON_DELETE_ = " ON DELETE ";
    private static final String _ON_UPDATE_ = " ON UPDATE ";

    private static final String _PRIMARY_KEY_ = " PRIMARY KEY ";

    private static final String CREATE_TABLE_ = "CREATE TABLE ";
    private static final String END_TABLE = "); ";

    private String table;
    private LinkedHashMap<String, String> pks;
    private LinkedHashMap<String, String> columns;
    private LinkedHashMap<String, String> fks;
    private ArrayList<String> uniques;

    public TableBuilder(String table) {
        this.table = table;
        // the key is the column name, the value is the column type
        columns = new LinkedHashMap<String, String>();
        pks = new LinkedHashMap<String, String>();
        fks = new LinkedHashMap<String, String>();
        // the pair key is the conflict policy and the internal list contains the column names
        // the external list holds all together
        uniques = new ArrayList<String>();
    }

    /**
     * create a unique constraint to a already declared column using the specified conflict policy.
     * ATENTION: It does not add the column to the table, you need to add the column using addColumn or addFK
     */
    public void makeUnique(String column, String conflictPolicy) {
        makeUnique(new String[]{column}, conflictPolicy);
    }

    /**
     * create a unique constraint to already declared columns using the specified conflict policy.
     * ATENTION: It does not add the columns to the table, you need to add the columns using addColumn or addFK
     */
    public void makeUnique(String[] columns, String conflictPolicy) {
        uniques.add(CONSTRAINT_UNIQUE_ + TextUtils.join("_", columns) + _UNIQUE__ + TextUtils.join(",", columns) + __ON_CONFLICT_ + conflictPolicy + COMMA);
    }

    public void setPrimaryKey(String column, String type, boolean autoInc) throws Exception {
        if (autoInc) {
            column = column + _AUTOINCREMENT_;
        }
        setPrimaryKey(new String[]{column}, new String[]{type});
    }

    public void setPrimaryKey(String[] columns, String[] types) throws Exception {
        int stopPoint = columns.length < types.length ? columns.length : types.length;
        for (int index = 0; index < stopPoint; ++index) {

            if (!pks.containsKey(columns[index])) {
                pks.put(columns[index], types[index]);
            } else {
                throw new Exception("Repeated columns!: " + columns[index]);
            }

            this.addColumn(columns[index].replace(_AUTOINCREMENT_, ""), types[index], false, false);

        }
    }

    /**
     * Add a column to the table
     *
     * @param column       the column name to be added to the table
     * @param type         the column type (INTEGER, TEXT, REAL, BLOB)
     * @param notNull      if this field will be allowed to be null or not
     * @param checkColumns verify if the column being added is repeated
     * @throws Exception if checkColumns is true and there is a repeated column
     */
    private void addColumn(String column, String type, boolean notNull, boolean checkColumns) throws Exception {
        if (!columns.containsKey(column)) {
            columns.put(column, column + " " + type + (notNull ? _NOT_NULL_ + COMMA : COMMA));
        } else if (checkColumns) {
            throw new Exception("Repeated columns!: " + column);
        }
    }

    /**
     * Add a column to the table with checkColumns is used as true
     *
     * @param column  the column name to be added to the table
     * @param type    the column type (INTEGER, TEXT, REAL, BLOB)
     * @param notNull if this field will be allowed to be null or not
     * @throws Exception if there is a repeated column
     */
    public void addColumn(String column, String type, boolean notNull) throws Exception {
        this.addColumn(column, type, notNull, true);
    }

    /**
     * Add a column to the table with checkColumns and notNull are used as true
     *
     * @param column the column name to be added to the table
     * @param type   the column type (INTEGER, TEXT, REAL, BLOB)
     * @throws Exception if there is a repeated column
     */
    public void addColumn(String column, String type) throws Exception {
        this.addColumn(column, type, true);
    }

    public void addFK(String column, String type, String tableRef, String columnRef, int actionDelete, int actionUpdate) throws Exception {
        this.addColumn(column, type, false, false);

        fks.put(column, CONSTRAINT_FK_ + column + _FOREIGN_KEY__ + column + __REFERENCES_ + tableRef + _OB_ + columnRef + _CB_ + _ON_DELETE_
                + ACTIONS[actionDelete] + _ON_UPDATE_ + ACTIONS[actionUpdate] + COMMA);

    }

    public void addFK_PKMultiple(String[] columns, String[] types, String tableRef, int actionDelete, int actionUpdate) throws Exception {
        int startPoint = columns.length < types.length ? columns.length - 1 : types.length - 1;
        String constraintName = "";
        String columnName = "";

        for (int index = startPoint; index > -1; --index) {
            if (index == startPoint) {
                constraintName = columns[index] + "__";
                columnName = columns[index];
            } else {
                constraintName = columns[index] + "_" + constraintName;
                columnName = columns[index] + COMMA + columnName;

            }
            this.addColumn(columns[index], types[index], false, false);
        }
        fks.put(columnName, CONSTRAINT_FK_ + constraintName + _FOREIGN_KEY__ + columnName + __REFERENCES_ + tableRef + _ON_DELETE_ + ACTIONS[actionDelete]
                + _ON_UPDATE_ + ACTIONS[actionUpdate] + COMMA);

    }

    public void addFK(String[] columns, String[] types, String tableRef, String[] columnsRef, int actionDelete, int actionUpdate) throws Exception {
        int stopPoint = columns.length < types.length ? columns.length : types.length;
        String constraintName = "";
        String columnsNames = "";
        String columnsNamesRef = "";

        for (int i = 0; i < stopPoint; ++i) {
            if (i == 0) {
                constraintName = columns[i] + "_";
                columnsNames = columns[i];
                columnsNamesRef = columnsRef[i];
            } else {
                constraintName = columns[i] + "_" + constraintName;
                columnsNames = columns[i] + COMMA + columnsNames;
                columnsNamesRef = columnsRef[i] + COMMA + columnsNamesRef;

            }
            this.addColumn(columns[i], types[i], false, false);
        }
        fks.put(columnsNames, CONSTRAINT_FK_ + constraintName + _FOREIGN_KEY__ + columnsNames + __REFERENCES_ + tableRef + _OB_ + columnsNamesRef + _CB_
                + _ON_DELETE_ + ACTIONS[actionDelete] + _ON_UPDATE_ + ACTIONS[actionUpdate] + COMMA);

    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(CREATE_TABLE_);
        sb.append(table);
        sb.append(_OB_);

        for (String col : columns.values()) {
            sb.append(col);
        }

        if (pks != null && pks.size() > 0) {
            sb.append(_PRIMARY_KEY_ + _OB_);

            int sizePk = pks.size();
            int i = 0;
            for (String pk : pks.keySet()) {
                if (i < sizePk - 1) {
                    sb.append(pk + COMMA);
                } else {
                    sb.append(pk);
                }
                i = i + 1;
            }

            sb.append(_CB_);
        }

        for (String fk : fks.values()) {
            sb.append(fk);
        }

        for (String unique : uniques) {
            sb.append(unique);
        }

        String test = sb.toString();

        if (test.endsWith(COMMA)) {
            String t = test.substring(0, test.length() - COMMA.length()) + END_TABLE;
            if (PRINT_CREATION) {
                System.out.println(t);
            }
            return t;

        } else {

            sb.append(END_TABLE);
            if (PRINT_CREATION) {
                System.out.println(sb.toString());
            }
            return sb.toString();
        }
    }

    public static String createIndexString(String table, String column) {
        return "CREATE INDEX " + "index_" + column + "_" + table + " ON " + table + "(" + column + ")";
    }

    public static String createIndexString(String table, String[] columns) {
        String columnsString = TextUtils.join(",", columns);
        String columnsIndexSufix = TextUtils.join("_", columns);
        return "CREATE INDEX " + "index_" + columnsIndexSufix + "_" + table + " ON " + table + "(" + columnsString + ")";
    }
}