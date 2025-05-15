from mcp import ClientSession, StdioServerParameters, types
from mcp.client.stdio import stdio_client

# Create server parameters for stdio connection
server_params = StdioServerParameters(
    command="node",  # Executable
    args=["/home/wlopez/download/mcp-course/servers/basic/dist/server.js"],  # Optional command line arguments
    env=None,  # Optional environment variables
)

async def run():
    async with stdio_client(server_params) as (reader, writer):
        async with ClientSession(reader, writer) as session:
            await session.initialize()

            """ List all prompts """
            prompts = await session.list_prompts()
            print("Prompts:")
            print(prompts)

            """ execute a prompt """
            print("\nExecuting prompt...")
            prompt = await session.get_prompt(
                prompts.prompts[1].name,
                arguments={
                    "code": "console.log('Hello, world!');"
                }
            )
            print("Prompt:")
            print(prompt)

            ### list resources allowable
            print("\nListing resources...")
            resources = await session.list_resources()
            print("Resources:")
            print(resources)

            ### list resource  dynamic allowable
            print("\nListing resource dynamic allowable...")
            template_resources = await session.list_resource_templates()
            print("template_resources:")
            print(template_resources)

            ### Obtener un recurso estatico
            print("\nGetting static resource...")
            resource = await session.read_resource('got://quotes/random')
            print("resource:")
            print(resource)

            ### Obtener un recurso dinamico
            print("\nGetting dynamic resource...")
            resource = await session.read_resource('person://properties/alexys')
            print("resource dynamic:")
            print(resource)

            ### list tools
            print("\nListing tools...")
            tools = await session.list_tools()
            print("tools:")
            print(tools)
            
            
            
            
            ### execute a tool
            print("\nExecuting tool...")
            tool = await session.call_tool(
                tools.tools[1].name,
                arguments={
                    "numbers": [10, 5 , 2]
                }
            )
            print("tool:")
            print(tool)



if __name__ == "__main__":
    import asyncio

    asyncio.run(run())