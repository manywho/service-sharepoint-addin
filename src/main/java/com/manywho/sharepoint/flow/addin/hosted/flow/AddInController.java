package com.manywho.sharepoint.flow.addin.hosted.flow;

import com.google.common.base.Strings;
import javax.ws.rs.*;


@Path("/callback")
public class AddInController {

    @Path("/run-flow-web-part")
    @Produces("text/html")
    @POST
    public String runFlowWebPart(@FormParam("SPAppToken") String contextToken, @QueryParam("editmode") String editMode,
                                 @QueryParam("flow-id") String flowId,
                                 @QueryParam("flow-version-id") String flowVersionId,
                                 @QueryParam("tenant-id") String tenantId,
                                 @QueryParam("admin-tenant-id") String adminTenantId,
                                 @QueryParam("host") String host,
                                 @QueryParam("player") String player,
                                 @QueryParam("mode") String mode
    ) {

        return this.runFlowInternal(contextToken, flowId, flowVersionId, tenantId, adminTenantId, host, player, mode);
    }

    @Path("/run-flow")
    @Produces("text/html")
    @POST
    public String runFlow(@FormParam("SPAppToken") String contextToken) {
        // todo allow configure the app to run standalone without a web part
        return this.runFlowInternal(contextToken, "", "", "", "", "",
                "", "");
    }

    private String runFlowInternal(String contextToken, String flowId, String flowVersionId, String tenantId,
                                   String adminTenantId, String host, String player, String mode) {

        return "<iframe src=\"https://flow.manywho.com/8b572d5b-76ba-473e-9e37-be06b6e8a396/play/default?join=ef9fb4c6-ce1a-416b-953f-99182cd86d0a\"></iframe>";
        
    }
}
