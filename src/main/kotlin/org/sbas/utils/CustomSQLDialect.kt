package org.sbas.utils

import org.hibernate.dialect.PostgreSQL10Dialect
import org.hibernate.dialect.function.StandardSQLFunction
import org.hibernate.type.StandardBasicTypes

class CustomMysqlDialect : PostgreSQL10Dialect() {
    init {
        registerFunction("fn_get_bed_asgn_stat", StandardSQLFunction("fn_get_bed_asgn_stat", StandardBasicTypes.STRING))
        registerFunction("fn_get_cd_nm", StandardSQLFunction("fn_get_cd_nm", StandardBasicTypes.STRING))
        registerFunction("split_part", StandardSQLFunction("split_part", StandardBasicTypes.STRING))
    }
}