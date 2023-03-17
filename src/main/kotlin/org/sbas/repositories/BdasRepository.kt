package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.bdas.*
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class BdasReqRepository : PanacheRepositoryBase<BdasReq, BdasReqId>

@ApplicationScoped
class BdasTrnRepository: PanacheRepositoryBase<BdasTrn, BdasTrnId>

@ApplicationScoped
class BdasAprvRepository: PanacheRepositoryBase<BdasAprv, BdasAprvId>

@ApplicationScoped
class BdasAdmRepository: PanacheRepositoryBase<BdasAdm, BdasAdmId>