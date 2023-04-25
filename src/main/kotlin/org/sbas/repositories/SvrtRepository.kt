package org.sbas.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import org.sbas.entities.svrt.SvrtPt
import org.sbas.entities.svrt.SvrtPtId
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SvrtPtRepository : PanacheRepositoryBase<SvrtPt, SvrtPtId>