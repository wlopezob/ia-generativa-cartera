import string
import random
import requests
from typing import Optional
from app.core.settings import settings

def generate_key(token_length: int) -> str:
    """
    Generates a random string to simulate a reCAPTCHA token
    
    Args:
        token_length (int): The desired token length
    
    Returns:
        str: A random string simulating a token
    """
    random_token = ""
    
    while len(random_token) < token_length:
        random_chars = ''.join(random.choice(string.ascii_lowercase + string.digits) 
                              for _ in range(8))
        random_token += random_chars
    
    return random_token[:token_length]

def consultar_ruc(nro_ruc: str) -> Optional[requests.Response]:
    """
    Queries information for a RUC from the SUNAT website.
    
    Args:
        nro_ruc: RUC number to query
    
    Returns:
        Response object or None in case of error
    """
    recaptcha_token = generate_key(52)
    url = settings.sunat_url
    
    headers = {
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7',
        'Accept-Language': 'en-PE,en;q=0.9,es-PE;q=0.8,es;q=0.7,en-GB;q=0.6,en-US;q=0.5',
        'Cache-Control': 'max-age=0',
        'Connection': 'keep-alive',
        'Content-Type': 'application/x-www-form-urlencoded',
        'Origin': 'https://e-consultaruc.sunat.gob.pe',
        'Referer': 'https://e-consultaruc.sunat.gob.pe/cl-ti-itmrconsruc/FrameCriterioBusquedaWeb.jsp',
        'Sec-Fetch-Dest': 'document',
        'Sec-Fetch-Mode': 'navigate',
        'Sec-Fetch-Site': 'same-origin',
        'Sec-Fetch-User': '?1',
        'Upgrade-Insecure-Requests': '1',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36',
        'sec-ch-ua': '"Not(A:Brand";v="99", "Google Chrome";v="133", "Chromium";v="133"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"Windows"'
    }
    
    data = {
        'accion': 'consPorRuc',
        'razSoc': '',
        'nroRuc': nro_ruc,
        'nrodoc': '',
        'token': recaptcha_token,
        'contexto': 'ti-it',
        'modo': '1',
        'rbtnTipo': '1',
        'search1': nro_ruc,
        'tipdoc': '1',
        'search2': '',
        'search3': '',
        'codigo': ''
    }
    
    try:
        with requests.Session() as session:
            response = session.post(
                url,
                headers=headers,
                data=data,
                timeout=settings.sunat_timeout
            )
            
            response.raise_for_status()
            return response
            
    except requests.exceptions.RequestException as err:
        # Log the error instead of printing
        import logging
        logging.error(f"Error querying RUC {nro_ruc}: {err}")
    
    return None 