# server.py
from mcp.server.fastmcp import FastMCP

# Create a FastMCP server
mcp = FastMCP("Calculator MCP Server")

def add(a: int, b: int) -> int:
    return a + b

def subtract(a: float, b: float) -> float:
    """
    Resta dos números flotantes.

    Args:
        a (float): El minuendo.
        b (float): El sustraendo.

    Returns:
        float: El resultado de a - b.
    """
    return a - b

def multiply(a: float, b: float) -> float:
    """
    Multiplica dos números flotantes.

    Args:
        a (float): El primer factor.
        b (float): El segundo factor.

    Returns:
        float: El producto de a y b.
    """
    return a * b

def divide(a: float, b: float) -> float:
    """
    Divide dos números flotantes.

    Args:
        a (float): El dividendo.
        b (float): El divisor.

    Returns:
        float: El resultado de a / b.

    Raises:
        ValueError: Si b es igual a cero.
    """
    if b == 0.0:
        raise ValueError("El divisor no puede ser cero.")
    return a / b

@mcp.tool()
def calculate(a: float, b: float, operation: str) -> float:
    """
    Realiza una operación matemática entre dos números.

    Args:
        a (float): El primer número.
        b (float): El segundo número.
        operation (str): La operación a realizar, add, subtract, multiply, divide

    Returns:
        float: El resultado de la operación.
    """ 
    if operation == "add":
        return add(a, b)
    elif operation == "subtract":
        return subtract(a, b)
    elif operation == "multiply":
        return multiply(a, b)
    elif operation == "divide":
        return divide(a, b)
    else:
        raise ValueError("Operación no válida.")    

if __name__ == "__main__":
    mcp.run(transport='stdio')






