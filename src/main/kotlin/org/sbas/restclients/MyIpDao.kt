package org.sbas.restclients

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(configKey = "myip-api")
interface MyIpDao {

  @GET
  @Path("")
  fun getMyIp() : String

}