package com.manywho.sharepoint.flow.addin.hosted.flow;

import com.google.common.base.Strings;

import javax.ws.rs.*;

@Path("/callback")
public class AddInController {

    private final String DEFAULT_FLOW = "https://flow.manywho.com/bb03e922-8a39-46e8-b492-aacd2ccb5a42/play/default/?flow-id=393badb0-14f4-4d5e-a79c-f00a323fc4a9";

    @Path("/run-flow-web-part")
    @Produces("text/html")
    @POST
    public String runFlowWebPart(@FormParam("SPAppToken") String contextToken,
                                 @QueryParam("editmode") String editMode,
                                 @QueryParam("flow-id") String flowId,
                                 @QueryParam("flow-version-id") String flowVersionId,
                                 @QueryParam("tenant-id") String tenantId,
                                 @QueryParam("admin-tenant-id") String adminTenantId,
                                 @QueryParam("host") String host,
                                 @QueryParam("player") String player,
                                 @QueryParam("mode") String mode) {

        return this.runFlowInternal(contextToken, flowId, flowVersionId, tenantId, adminTenantId, host, player, mode);
    }

    @Path("/run-flow")
    @Produces("text/html")
    @POST
    public String runFlow(@FormParam("SPAppToken") String contextToken) {
        // todo allow configure the app to run standalone without a web part
        return pageWithFlowInIframe(DEFAULT_FLOW);
    }

    @Path("/run-flow")
    @Produces("text/html")
    @GET
    public String runFlowGet() {

        // todo allow configure the app to run standalone without a web part
        return pageWithFlowInIframe(DEFAULT_FLOW);
    }

    private String runFlowInternal(String contextToken, String flowId, String flowVersionId, String tenantId,
                                   String adminTenantId, String host, String player, String mode) {

        try {
            if (Strings.isNullOrEmpty(tenantId) || Strings.isNullOrEmpty(flowId) || Strings.isNullOrEmpty(contextToken) || Strings.isNullOrEmpty(player)) {
                return pageWithFlowInIframe(DEFAULT_FLOW);
            } else if (Strings.isNullOrEmpty(flowVersionId)){
                return pageWithFlowInIframe(String.format("https://flow.manywho.com/%s/play/%s/?flow-id=%s&session-token=%s",
                        tenantId, player, flowId, contextToken));
            } else {
                return pageWithFlowInIframe(String.format("https://flow.manywho.com/%s/play/%s/?flow-id=%s&flow-version-id=%s&session-token=%s",
                        tenantId, player, flowId, flowVersionId, contextToken));
            }

        } catch (Exception e) {

            // if there is an exception initialization of the flow I run an specific flow with very basic information
            return pageWithFlowInIframe(DEFAULT_FLOW);
        }
    }


    private String pageWithFlowInIframe(String joinUrl) {
        String ifFrame = String.format("<iframe  src=\"%s\" frameborder=\"0\" style=\"overflow:hidden;height:calc(100vh - 300px);width:100%%\" height=\"100%%\" width=\"100%%\"></iframe>", joinUrl);

        return String.format("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>title</title></head><body>%s</body></html>", ifFrame);
    }
}
