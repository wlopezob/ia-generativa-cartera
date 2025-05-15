import { Client } from "@modelcontextprotocol/sdk/client/index.js";
import { StdioClientTransport } from "@modelcontextprotocol/sdk/client/stdio.js";

// Create a transport to connect to the server
const transport = new StdioClientTransport({
    command: "node",
    args: ["/home/wlopez/download/mcp-course/servers/basic/dist/server.js"]
});

const client = new Client(
    {
        name: "basic-client",
        version: "1.0.0"
    },
    {
        capabilities: {
            tools: {}
        }
    }
);

// Connect to the server
await client.connect(transport);

// List prompts
// https://github.com/modelcontextprotocol/typescript-sdk?tab=readme-ov-file#writing-mcp-clients
const prompts = await client.listPrompts();
console.log(JSON.stringify(prompts, null, 2));

// List resources
const resources = await client.listResources();
console.log(JSON.stringify(resources, null, 2));

// list template resources
const templateResources = await client.listResourceTemplates();
console.log(JSON.stringify(templateResources, null, 2));

// list tool resources
const toolResources = await client.listTools();
console.log(JSON.stringify(toolResources, null, 2));

// Get a prompt
console.log("############### Prompt ###############");
const prompt = await client.getPrompt({
    name: prompts.prompts[1].name,
    arguments: {
        code: "print('Hello, world!')"
    }

});
console.log(JSON.stringify(prompt, null, 2));

// Get a resource
console.log("############### Resource ###############");
const resource = await client.readResource({
    uri: "got://quotes/random"
});
console.log(JSON.stringify(resource, null, 2));

// Get template resource
console.log("############### Template Resource ###############");
const templateResource = await client.readResource({
    uri:  "person://properties/alexys"
});
console.log(JSON.stringify(templateResource, null, 2));

// Get a tool
console.log("############### Tool ###############");
const tool = await client.callTool({
    name: toolResources.tools[1].name,
    arguments: {
        numbers: [1, 2, 3, 4, 5]
    }
});
console.log(JSON.stringify(tool, null, 2));
