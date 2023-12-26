package org.sbas.utils

import org.hibernate.boot.model.FunctionContributions
import org.hibernate.boot.model.FunctionContributor
import org.hibernate.dialect.function.StandardSQLFunction
import org.hibernate.type.StandardBasicTypes

class CustomFunctionDistributor : FunctionContributor {

    override fun contributeFunctions(functionContributions: FunctionContributions) {
        val stringType = StandardBasicTypes.STRING
        val integerType = StandardBasicTypes.INTEGER
        val booleanType = StandardBasicTypes.BOOLEAN

        functionContributions.functionRegistry.register("fn_get_bed_asgn_stat", StandardSQLFunction("fn_get_bed_asgn_stat", stringType))
        functionContributions.functionRegistry.register("fn_get_cd_nm", StandardSQLFunction("fn_get_cd_nm", stringType))
        functionContributions.functionRegistry.register("fn_get_dstr_cd2_nm", StandardSQLFunction("fn_get_dstr_cd2_nm", stringType))
        functionContributions.functionRegistry.register("fn_get_age", StandardSQLFunction("fn_get_age", integerType))
        functionContributions.functionRegistry.register("fn_find_chrg_inst", StandardSQLFunction("fn_find_chrg_inst", stringType))
        functionContributions.functionRegistry.register("fn_like_any", StandardSQLFunction("fn_like_any", booleanType))
    }

}