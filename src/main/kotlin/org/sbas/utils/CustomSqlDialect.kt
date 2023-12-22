package org.sbas.utils

import org.hibernate.boot.model.FunctionContributions
import org.hibernate.boot.model.FunctionContributor
import org.hibernate.dialect.PostgreSQLDialect
import org.hibernate.dialect.function.StandardSQLFunction
import org.hibernate.type.BasicType
import org.hibernate.type.StandardBasicTypes

/**
 * 새로 만든 DB 함수를 JPQL 에서 사용하려면 아래의 클래스에 등록해줘야 합니다.
 */
//class CustomSqlDialect : PostgreSQLDialect() {
//    init {
////        registerFunction("fn_get_bed_asgn_stat", StandardSQLFunction("fn_get_bed_asgn_stat", StandardBasicTypes.STRING))
////        registerFunction("fn_get_cd_nm", StandardSQLFunction("fn_get_cd_nm", StandardBasicTypes.STRING))
////        registerFunction("fn_get_dstr_cd2_nm", StandardSQLFunction("fn_get_dstr_cd2_nm", StandardBasicTypes.STRING))
////        registerFunction("fn_get_age", StandardSQLFunction("fn_get_age", StandardBasicTypes.INTEGER))
////        registerFunction("fn_find_chrg_inst", StandardSQLFunction("fn_find_chrg_inst", StandardBasicTypes.STRING))
////        registerFunction("fn_like_any", StandardSQLFunction("fn_like_any", StandardBasicTypes.BOOLEAN))
//    }
//}

class CustomSqlDialect(): FunctionContributor {
    override fun contributeFunctions(functionContributions: FunctionContributions) {
        val stringType = functionContributions.typeConfiguration.basicTypeRegistry.resolve(StandardBasicTypes.STRING)
        val integerType = functionContributions.typeConfiguration.basicTypeRegistry.resolve(StandardBasicTypes.INTEGER)
        val booleanType = functionContributions.typeConfiguration.basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN)

        functionContributions.functionRegistry.registerNoArgs("fn_get_bed_asgn_stat", stringType)
        functionContributions.functionRegistry.registerNoArgs("fn_get_cd_nm", stringType)
        functionContributions.functionRegistry.registerNoArgs("fn_get_dstr_cd2_nm", stringType)
        functionContributions.functionRegistry.registerNoArgs("fn_get_age", integerType)
        functionContributions.functionRegistry.registerNoArgs("fn_find_chrg_inst", stringType)
        functionContributions.functionRegistry.registerNoArgs("fn_like_any", booleanType)
    }
}