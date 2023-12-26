package org.sbas.utils

import org.hibernate.boot.model.FunctionContributions
import org.hibernate.boot.model.FunctionContributor
import org.hibernate.dialect.function.StandardSQLFunction
import org.hibernate.type.StandardBasicTypes

/**
 * 새로 만든 DB 함수를 JPQL 에서 사용하려면 아래의 클래스에 등록해줘야 합니다.
 */
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