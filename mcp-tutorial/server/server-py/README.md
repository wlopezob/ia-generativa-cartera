## SDK
https://github.com/modelcontextprotocol/python-sdk?tab=readme-ov-file#quickstart

## execute
uv run server.py

## inspector
```bash
npx @modelcontextprotocol/inspector
```

### configuration
#### https://modelcontextprotocol.io/quickstart/server
```python
{
    "mcpServers": {
        "weather": {
            "command": "uv",
            "args": [
                "--directory",
                "/ABSOLUTE/PATH/TO/PARENT/FOLDER/weather",
                "run",
                "weather.py"
            ]
        }
    }
}
```

#### configuration inside inspector
```
command: uv
arguments: --directory /home/wlopez/rust/ia-generativa-cartera/mcp-tutorial/server/server-py run server.py
```