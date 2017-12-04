package com.manywho.sharepoint.flow.addin.hosted.flow;

import com.google.common.base.Strings;
import com.manywho.sdk.client.flow.FlowClient;
import com.manywho.sdk.client.flow.FlowInitializationOptions;
import com.manywho.sdk.client.flow.FlowState;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.UUID;


@Path("/callback")
public class AddInController {

    private FlowClient flowClient;

    @Inject
    public AddInController(FlowClient flowClient) {
        this.flowClient = flowClient;
    }

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

        UUID tenantUuid = null;
        UUID flowIdUuid = null;
        UUID flowVersionIdUuid = null;

        if (!Strings.isNullOrEmpty(flowId)) {
            flowIdUuid = UUID.fromString(flowId);
        }

        if (!Strings.isNullOrEmpty(flowVersionId)) {
            flowVersionIdUuid = UUID.fromString(flowVersionId);
        }

        if (!Strings.isNullOrEmpty(tenantId)) {
            tenantUuid = UUID.fromString(tenantId);
        }

        FlowInitializationOptions options = new FlowInitializationOptions();
        FlowState flowState = flowClient.start(tenantUuid, flowIdUuid, flowVersionIdUuid, options);

        String page = String.format("<iframe src=\"https://%s/%s/play/default?join=%s\"></iframe>",
                host, tenantId, flowState.getState().toString());

        if (Strings.isNullOrEmpty(mode) || "DEFAULT".equals(mode)) {
            mode = "null";
        }

        String navigationElementId = "null";
        String reportingMode = "null";
        String theme = "null";
        String join = "null";
        String authorization = "null";
        String initialization = "null";

        String template =  "<!DOCTYPE html>\n" +
                "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" class=\"manywho\" style=\"height: 100%;\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no\">\n" +
                "    <title>ManyWho</title>\n" +
                "    <style>\n" +
                "        .mw-bs .wait-container {\n" +
                "            position: absolute;\n" +
                "            top: 0;\n" +
                "            left: 0;\n" +
                "            width: 100%;\n" +
                "            height: 100%;\n" +
                "            min-height: 500px;\n" +
                "            z-index: 1100;\n" +
                "            background-color: rgba(255, 255, 255, 0.5);\n" +
                "        }\n" +
                "\n" +
                "        .mw-bs .wait-message {\n" +
                "            position: relative;\n" +
                "            text-align: center;\n" +
                "            margin-top: 1em;\n" +
                "            display: block;\n" +
                "            top: 40%;\n" +
                "            font-size: 2em;\n" +
                "            padding: 0 2em;\n" +
                "        }\n" +
                "\n" +
                "        /* outer */\n" +
                "        .mw-bs .wait-spinner {\n" +
                "            display: block;\n" +
                "            position: relative;\n" +
                "            left: 50%;\n" +
                "            width: 150px;\n" +
                "            height: 150px;\n" +
                "            margin: 200px 0 0 -75px;\n" +
                "            border-radius: 50%;\n" +
                "            border: 3px solid transparent;\n" +
                "            border-top-color: #268AAF;\n" +
                "            -webkit-animation: spin 2s linear infinite; /* Chrome, Opera 15+, Safari 5+ */\n" +
                "            animation: spin 2s linear infinite; /* Chrome, Firefox 16+, IE 10+, Opera */\n" +
                "        }\n" +
                "\n" +
                "        /* middle */\n" +
                "        .mw-bs .wait-spinner:before {\n" +
                "            content: \"\";\n" +
                "            position: absolute;\n" +
                "            top: 5px;\n" +
                "            left: 5px;\n" +
                "            right: 5px;\n" +
                "            bottom: 5px;\n" +
                "            border-radius: 50%;\n" +
                "            border: 3px solid transparent;\n" +
                "            border-top-color: #31B2E2;\n" +
                "            -webkit-animation: spin 3s linear infinite; /* Chrome, Opera 15+, Safari 5+ */\n" +
                "              animation: spin 3s linear infinite; /* Chrome, Firefox 16+, IE 10+, Opera */\n" +
                "        }\n" +
                "\n" +
                "        /* inner */\n" +
                "        .mw-bs .wait-spinner:after {\n" +
                "            content: \"\";\n" +
                "            position: absolute;\n" +
                "            top: 15px;\n" +
                "            left: 15px;\n" +
                "            right: 15px;\n" +
                "            bottom: 15px;\n" +
                "            border-radius: 50%;\n" +
                "            border: 3px solid transparent;\n" +
                "            border-top-color: #154E62;\n" +
                "            -webkit-animation: spin 1.5s linear infinite; /* Chrome, Opera 15+, Safari 5+ */\n" +
                "              animation: spin 1.5s linear infinite; /* Chrome, Firefox 16+, IE 10+, Opera */\n" +
                "        }\n" +
                "\n" +
                "        @-webkit-keyframes spin {\n" +
                "            0%   {\n" +
                "                -webkit-transform: rotate(0deg);  /* Chrome, Opera 15+, Safari 3.1+ */\n" +
                "                -ms-transform: rotate(0deg);  /* IE 9 */\n" +
                "                transform: rotate(0deg);  /* Firefox 16+, IE 10+, Opera */\n" +
                "            }\n" +
                "            100% {\n" +
                "                -webkit-transform: rotate(360deg);  /* Chrome, Opera 15+, Safari 3.1+ */\n" +
                "                -ms-transform: rotate(360deg);  /* IE 9 */\n" +
                "                transform: rotate(360deg);  /* Firefox 16+, IE 10+, Opera */\n" +
                "            }\n" +
                "        }\n" +
                "        @keyframes spin {\n" +
                "            0%   {\n" +
                "                -webkit-transform: rotate(0deg);  /* Chrome, Opera 15+, Safari 3.1+ */\n" +
                "                -ms-transform: rotate(0deg);  /* IE 9 */\n" +
                "                transform: rotate(0deg);  /* Firefox 16+, IE 10+, Opera */\n" +
                "            }\n" +
                "            100% {\n" +
                "                -webkit-transform: rotate(360deg);  /* Chrome, Opera 15+, Safari 3.1+ */\n" +
                "                -ms-transform: rotate(360deg);  /* IE 9 */\n" +
                "                transform: rotate(360deg);  /* Firefox 16+, IE 10+, Opera */\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body style=\"height: 100%;\">\n" +
                "<div id=\"manywho\">\n" +
                "    <div id=\"loader\" class=\"mw-bs\" style=\"width: 100%; height: 100%;\">\n" +
                "        <div class=\"wait-container\">\n" +
                "            <div class=\"wait-spinner\"></div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "\n" +
                "<script src=\"https://assets.manywho.com/js/vendor/jquery-2.1.4.min.js\"></script>\n" +
                "\n" +
                "<script>\n" +
                "    //syncOnUnload: false,\n" +
                "    var manywho = {\n" +
                "        cdnUrl: \"https://assets.manywho.com\",\n" +
                "        initialize: function () {\n" +
                "            manywho.settings.initialize({\n" +
                "                adminTenantId: \"{{adminTenantId}}\",\n" +
                "                playerUrl: [\"https\", '//', \"{{host}}\", \"/{{tenantId}}/play/{{player}}\"].join(''),\n" +
                "                joinUrl: [\"https\", '//', \"{{host}}\", \"/{{tenantId}}/play/{{player}}\"].join(''),\n" +
                "                platform: { \"uri\": \"https://\"+\"{{host}}\"}\n" +
                "            });\n" +
                "\n" +
                "            var options = {\n" +
                "                authentication: {\n" +
                "                    sessionId: \"{{accessToken}}\",\n" +
                "                    sessionUrl: null\n" +
                "                },\n" +
                "                navigationElementId: {{navigationElementId}},\n" +
                "                mode: {{mode}},\n" +
                "                reportingMode: {{reportingMode}},\n" +
                "                replaceUrl: false,\n" +
                "                collaboration: {\n" +
                "                    isEnabled: false\n" +
                "                },\n" +
                "                inputs: null,\n" +
                "                annotations: null,\n" +
                "                navigation: {\n" +
                "                    isFixed: true,\n" +
                "                    isWizard: false\n" +
                "                },\n" +
                "                callbacks: [],\n" +
                "                theme: {{theme}}\n" +
                "            };\n" +
                "\n" +
                "            console.log(options);\n" +
                "\n" +
                "            manywho.engine.initialize(\n" +
                "                \"{{tenantId}}\",\n" +
                "                \"{{flowId}}\",\n" +
                "                \"{{flowVersionId}}\",\n" +
                "                'main',\n" +
                "                {{join}},\n" +
                "                {{authorization}},\n" +
                "                options,\n" +
                "                {{initialization}}\n" +
                "            );\n" +
                "\n" +
                "        }\n" +
                "    };\n" +
                "</script>\n" +
                "\n" +
                "<script src=\"https://assets.manywho.com/js/loader.min.js\"></script>"+
                "\n" +
                "</body>\n" +
                "</html>\n";

        template = template.replace("{{tenantId}}", tenantId);
        template = template.replace("{{adminTenantId}}", adminTenantId);
        template = template.replace("{{flowId}}", flowId);
        template = template.replace("{{flowVersionId}}", flowVersionId);
        template = template.replace("{{adminTenant}}", adminTenantId);
        template = template.replace("{{host}}", host);
        template = template.replace("{{player}}", player);
        template = template.replace("{{navigationElementId}}", navigationElementId);
        template = template.replace("{{mode}}", mode);
        template = template.replace("{{reportingMode}}", reportingMode);
        template = template.replace("{{theme}}", theme);
        template = template.replace("{{join}}", join);
        template = template.replace("{{authorization}}", authorization);
        template = template.replace("{{initialization}}", initialization);
        template = template.replace("{{accessToken}}", contextToken);

        //return template;
        page = "https://flow.manywho.com/8b572d5b-76ba-473e-9e37-be06b6e8a396/play/default?join=4acafa83-a67a-40c1-8ce5-57954cf6725d";
        return page;
    }
}
