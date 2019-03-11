package fixedass.barcode.com.fixed_ass_app.util;

/**
 * Created by Administrator on 2018/3/6.
 */

public class Constant {
    public static final String DATABASE_NAME = "fixed_ass.db";   //数据库名称
    public static final int DATABASE_VERSION = 1;   //数据库的版本号
    public static final String TABLE_NAME_USER = "tb_acl_user"; //表名
    public static final String TABLE_NAME_STORAGE = "tb_ocl_storage"; //表名
    public static final String TABLE_NAME_ADDRESS = "tb_ocl_address"; //表名
    public static final String TABLE_NAME_DEPT = "tb_acl_department"; //表名
    public static final String TABLE_NAME_COUNTBILL = "tb_ocl_countbill"; //表名
    public static final String TABLE_NAME_COUNT_DETAIL = "tb_ocl_count_detail"; //表名
    public static final String TABLE_NAME_PEOPLE = "tb_ocl_people"; //表名
    public static final String TABLE_NAME_SYSTEM = "tb_acl_system_info";

    //MySQL的连接参数
    public static final String DRIVER = "com.mysql.jdbc.Driver";
    public static final String URLPRE = "jdbc:mysql://";
    public static final String URLSUF = "/fixed_ass?characterEncoding=utf-8&serverTimezone=UTC";
    public static final String USER = "root";

    public static final int tableRowCount = 10;

    //sql语句
    public static final String USER_CON_SQL = "userUUID, deptUUID, userName, userPWD, userNo, sysUUID, creatorID, userState";
    public static final String DEPT_CON_SQL = "deptUUID, pDeptUUID, deptName, deptType, deptCode, sysUUID";
    public static final String ADD_CON_SQL = "addrUUID, deptUUID, addrName";
    public static final String STO_CON_SQL = "barCode,assName,className,assType,assPrice,bookDate, " +
            "useGroup,useCompany, useDept,usePeople, storeAddress, sysUUID";
    public static final String STO_CON_SEL = "barCode,assName,className,assType,assPrice,bookDate," +
            "useCompany, useDept,usePeople, storeAddress, sysUUID";
    public static final String BILL_CON_SQL = "createDate, countBillCode, createPeople, countNote, sysUUID";
    public static final String PEO_CON_SQL = "pUUID, pName, sysUUID";
    public static final String DETAIL_CON_SQL = "countUUID, countbillCode, barCode, countGroup,countCompany," +
            "countDepartment, countPlace, countPeople, countTime, countState, sysUUID";

    public static final String FILENAME = "ipConfig";      //定义文件名称
}
