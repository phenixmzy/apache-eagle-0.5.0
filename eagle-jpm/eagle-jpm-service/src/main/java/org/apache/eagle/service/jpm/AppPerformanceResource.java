package org.apache.eagle.service.jpm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("appPerformance")
public class AppPerformanceResource {
    private static final Logger LOG = LoggerFactory.getLogger(AppPerformanceResource.class);

    @GET
    @Path("elephant")
    public AppPerformanceResponse getDrElephant(@QueryParam("site") String site) {
        AppPerformanceResponse response = new AppPerformanceResponse();
        try {
            if (site == null) {
                throw new Exception("Invalid query parameters: site == null || queue == null || currentTime == 0L || top == 0");
            }
            LOG.info("AppPerformanceResource hanler appPerformance/elephant siteId:{}", site);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        response.setDeElephantUrl("www.163.com");
        return response;
    }

}
