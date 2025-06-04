from google.adk.runners import InMemoryRunner
from app.agent.agent import root_agent
from app.core.config import settings

runner = InMemoryRunner(app_name=settings.app_name, agent=root_agent) 