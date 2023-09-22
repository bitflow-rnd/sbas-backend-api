package org.sbas.utils

import org.hibernate.dialect.PostgreSQL10Dialect
import org.hibernate.dialect.function.StandardSQLFunction
import org.hibernate.type.StandardBasicTypes

/**
 * 새로 만든 DB 함수를 JPQL 에서 사용하려면 아래의 클래스에 등록해줘야 합니다.
 */
class CustomSqlDialect : PostgreSQL10Dialect() {
    init {
        registerFunction("fn_get_bed_asgn_stat", StandardSQLFunction("fn_get_bed_asgn_stat", StandardBasicTypes.STRING))
        registerFunction("fn_get_cd_nm", StandardSQLFunction("fn_get_cd_nm", StandardBasicTypes.STRING))
        registerFunction("fn_get_dstr_cd2_nm", StandardSQLFunction("fn_get_dstr_cd2_nm", StandardBasicTypes.STRING))
        registerFunction("fn_get_age", StandardSQLFunction("fn_get_age", StandardBasicTypes.INTEGER))
        registerFunction("fn_find_chrg_inst", StandardSQLFunction("fn_find_chrg_inst", StandardBasicTypes.STRING))
        registerFunction("fn_like_any", StandardSQLFunction("fn_like_any", StandardBasicTypes.BOOLEAN))
    }
}