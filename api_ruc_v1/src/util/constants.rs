pub const PROMPT: &str = r##"
Extract relevant business or personal data from the provided website text and return it in a structured JSON format.

Extract the following information from the provided text: Número de RUC, NombrePersonaOrEmpresa, Nombre Comercial, Estado del Contribuyente, Condición del Contribuyente, and Domicilio Fiscal. Return this information in JSON format with a "status" key that is true if at least one piece of information is found, or false if none are found. If "status" is false, all other values should be empty strings.

# Output Format

The output should be a JSON object with the following keys:
- "status": A boolean value, true if at least one data point is extracted, and false otherwise.
- "ruc": Always a string, include the extracted data or an empty string if not found.
- "nombrePersonaEmpresa": Always a string, include the extracted data or an empty string if not found.
- "nombreComercial": Always a string, include the extracted data or an empty string if not found.
- "estadoContribuyente": Always a string, include the extracted data or an empty string if not found.
- "condicionContribuyente": Always a string, include the extracted data or an empty string if not found.
- "domicilioFiscal": Always a string, include the extracted data or an empty string if not found.

# Example

**Input:**

"Texto de una página web que incluye el Número de RUC 123456789, una empresa llamada EjemploCorp, Estado del Contribuyente: Activo, Condición del Contribuyente: Habido, y Domicilio Fiscal: Calle Falsa 123."

**Output:**

{
  "status": true,
  "ruc": "123456789",
  "nombrePersonaEmpresa": "EjemploCorp",
  "nombreComercial": "",
  "estadoContribuyente": "Activo",
  "condicionContribuyente": "Habido",
  "domicilioFiscal": "Calle Falsa 123"
}

# Notes

- Ensure "status" is correctly set based on the presence or absence of data.
- Each extracted element should be checked thoroughly to ensure accuracy.
- If only partial data is found, the "status" should still be true, with empty strings for missing elements.
- Return only the json with the extracted data, do not return any additional text, do not return format markdown.  
"##;

pub const DATA_DEMO: &str = r##"
<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="initial-scale = 1.0, user-scalable = no">
    <title>SUNAT - Consulta RUC</title>

    <!-- Bootstrap -->
    <link href="/a/js/libs/bootstrap/3.3.1/css/bootstrap.min.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

    <style type="text/css">
        h1 {
            font-size: 1.7em;
        }

        .header div {
            padding-left: 0 !important;
        }

        .imgLogo {
            height: 2.5em;
            margin-top: 1em;
        }

        .text-left-important {
            text-align: left !important;
        }

        .cmbTipo {
            font-size: 0.8em !important;
        }

        .divBotonera {
            margin-bottom: 20px;
        }

        /*
        .table > thead > tr:first-child > td, 
		.table > tbody > tr:first-child > td {
        */
        .tblResultado>tbody>tr:first-child>td {
            border: none;
        }

        .divBotoneraArriba {
            margin-bottom: 15px;
        }


        /********************************************************************/
        /*** PANEL PRIMARY ***/
        .with-nav-tabs.panel-primary .nav-tabs>li>a,
        .with-nav-tabs.panel-primary .nav-tabs>li>a:hover,
        .with-nav-tabs.panel-primary .nav-tabs>li>a:focus {
            color: #fff;
        }

        .with-nav-tabs.panel-primary .nav-tabs>.open>a,
        .with-nav-tabs.panel-primary .nav-tabs>.open>a:hover,
        .with-nav-tabs.panel-primary .nav-tabs>.open>a:focus,
        .with-nav-tabs.panel-primary .nav-tabs>li>a:hover,
        .with-nav-tabs.panel-primary .nav-tabs>li>a:focus {
            color: #fff;
            background-color: #3071a9;
            border-color: transparent;
        }

        .with-nav-tabs.panel-primary .nav-tabs>li.active>a,
        .with-nav-tabs.panel-primary .nav-tabs>li.active>a:hover,
        .with-nav-tabs.panel-primary .nav-tabs>li.active>a:focus {
            color: #428bca;
            background-color: #fff;
            border-color: #428bca;
            border-bottom-color: transparent;
        }

        .with-nav-tabs.panel-primary .nav-tabs>li.dropdown .dropdown-menu {
            background-color: #428bca;
            border-color: #3071a9;
        }

        .with-nav-tabs.panel-primary .nav-tabs>li.dropdown .dropdown-menu>li>a {
            color: #fff;
        }

        .with-nav-tabs.panel-primary .nav-tabs>li.dropdown .dropdown-menu>li>a:hover,
        .with-nav-tabs.panel-primary .nav-tabs>li.dropdown .dropdown-menu>li>a:focus {
            background-color: #3071a9;
        }

        .with-nav-tabs.panel-primary .nav-tabs>li.dropdown .dropdown-menu>.active>a,
        .with-nav-tabs.panel-primary .nav-tabs>li.dropdown .dropdown-menu>.active>a:hover,
        .with-nav-tabs.panel-primary .nav-tabs>li.dropdown .dropdown-menu>.active>a:focus {
            background-color: #4a9fe9;
        }

        /*-------------------*/
    </style>
</head>

<body>

    <form action="/cl-ti-itmrconsruc/jcrS00Alias" method="post" name="selecXNroRuc">
        <input type="hidden" name="accion" value="consPorRazonSoc">
        <input type="hidden" name="razSoc" value="">
        <input type="hidden" name="contexto" value="ti-it">
        <input type="hidden" name="modo" value="1">
        <input type="hidden" name="tipdoc" value="1">
        <input type="hidden" name="rbtnTipo" value="3">
        <input type="hidden" name="search3" value="">
        <input type="hidden" name="numRnd" value="">
    </form>



    <div class="container">
        <div class="header hidden" id="divHeader">
            <div class="col-md-12">
                <img src="/a/imagenes/logo_2015.png" class="imgLogo">
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
                <div>
                    <h1>Consulta RUC</h1>

                </div>
                <div class="divBotoneraArriba text-center hidden-print">

                    <a href='FrameCriterioBusquedaWeb.jsp' class="hidden" id="aNuevaConsulta">Volver</a>
                    <button type="button" class="btn btn-danger btnNuevaConsulta">Volver</button>

                </div>


                <div class="panel panel-primary">
                    <div class="panel-heading">Resultado de la Búsqueda</div>
                    <div class="list-group">
                        <!-- Inicio filas de datos -->

                        <div class="list-group-item">
                            <div class="row">
                                <div class="col-sm-5">
                                    <h4 class="list-group-item-heading">N&uacute;mero de RUC:</h4>
                                </div>
                                <div class="col-sm-7">
                                    <h4 class="list-group-item-heading">20573215045 - LEAD WORKING PARTNER SAC</h4>
                                </div>
                            </div>
                        </div>
                        <div class="list-group-item">
                            <div class="row">
                                <div class="col-sm-5">
                                    <h4 class="list-group-item-heading">Tipo Contribuyente:</h4>
                                </div>
                                <div class="col-sm-7">
                                    <p class="list-group-item-text">SOCIEDAD ANONIMA CERRADA</p>
                                </div>
                            </div>
                        </div>

                        <div class="list-group-item">
                            <div class="row">
                                <div class="col-sm-5">
                                    <h4 class="list-group-item-heading">Nombre Comercial:</h4>
                                </div>
                                <div class="col-sm-7">
                                    <p class="list-group-item-text">-

                                    </p>
                                </div>
                            </div>
                        </div>

                        <div class="list-group-item">
                            <div class="row">
                                <div class="col-sm-3">
                                    <h4 class="list-group-item-heading">Fecha de Inscripci&oacute;n:</h4>
                                </div>
                                <div class="col-sm-3">
                                    <p class="list-group-item-text">05/02/2014</p>
                                </div>

                                <div class="col-sm-3">
                                    <h4 class="list-group-item-heading">Fecha de Inicio de Actividades:</h4>
                                </div>
                                <div class="col-sm-3">
                                    <p class="list-group-item-text">01/03/2014</p>
                                </div>
                            </div>
                        </div>


                        <div class="list-group-item list-group-item-success">
                            <div class="row">
                                <div class="col-sm-5">
                                    <h4 class="list-group-item-heading">Estado del Contribuyente:</h4>
                                </div>
                                <div class="col-sm-7">

                                    <p class="list-group-item-text">ACTIVO



                                    </p>
                                </div>
                            </div>
                        </div>



                        <div class="list-group-item list-group-item-success">
                            <div class="row">
                                <div class="col-sm-5">
                                    <h4 class="list-group-item-heading">Condici&oacute;n del Contribuyente:</h4>
                                </div>
                                <div class="col-sm-7">

                                    <p class="list-group-item-text">

                                        HABIDO


                                    </p>
                                </div>
                            </div>
                        </div>


                        <div class="list-group-item">
                            <div class="row">
                                <div class="col-sm-5">
                                    <h4 class="list-group-item-heading">Domicilio Fiscal:</h4>
                                </div>
                                <div class="col-sm-7">
                                    <p class="list-group-item-text">JR. AREQUIPA NRO. 1085 (CERCA AL COLEGIO MARIANO
                                        BONIN) HUANUCO - LEONCIO PRADO - RUPA-RUPA</p>
                                </div>
                            </div>
                        </div>


                        <div class="list-group-item">
                            <div class="row">
                                <div class="col-sm-3">
                                    <h4 class="list-group-item-heading">Sistema Emisión de Comprobante:</h4>
                                </div>
                                <div class="col-sm-3">
                                    <p class="list-group-item-text">COMPUTARIZADO</p>
                                </div>

                                <div class="col-sm-3">
                                    <h4 class="list-group-item-heading">Actividad Comercio Exterior:</h4>
                                </div>
                                <div class="col-sm-3">
                                    <p class="list-group-item-text">SIN ACTIVIDAD</p>
                                </div>
                            </div>
                        </div>

                        <div class="list-group-item">
                            <div class="row">
                                <div class="col-sm-5">
                                    <h4 class="list-group-item-heading">Sistema Contabilidad:</h4>
                                </div>
                                <div class="col-sm-7">
                                    <p class="list-group-item-text">MANUAL</p>
                                </div>
                            </div>
                        </div>



                        <div class="list-group-item">
                            <div class="row">
                                <div class="col-sm-5">
                                    <h4 class="list-group-item-heading">Actividad(es) Econ&oacute;mica(s):</h4>
                                </div>
                                <div class="col-sm-7">
                                    <table class="table tblResultado">
                                        <tbody>

                                            <tr>
                                                <td>Principal - 6202 - CONSULTORÍA DE INFORMÁTICA Y GESTIÓN DE
                                                    INSTALACIONES INFORMÁTICAS</td>
                                            </tr>
                                            <!--SC003-2015 Inicio-->

                                            <!--SC003-2015 Fin-->

                                            <tr>
                                                <td>Secundaria 1 - 6201 - PROGRAMACIÓN INFORMÁTICA</td>
                                            </tr>
                                            <!--SC003-2015 Inicio-->

                                            <!--SC003-2015 Fin-->


                                            <tr>
                                                <td>Secundaria 2 - 4741 - VENTA AL POR MENOR DE ORDENADORES, EQUIPO
                                                    PERIFÉRICO, PROGRAMA DE INFORM. Y EQU. DE TELEC. EN COMERCIOS
                                                    ESPECIALIZADOS</td>
                                            </tr>
                                            <!--SC003-2015 Inicio-->

                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>



                        <div class="list-group-item">
                            <div class="row">
                                <div class="col-sm-5">
                                    <h4 class="list-group-item-heading">Comprobantes de Pago c/aut. de impresión (F. 806
                                        u 816):</h4>
                                </div>
                                <div class="col-sm-7">
                                    <table class="table tblResultado">
                                        <tbody>






                                            <tr>
                                                <td>FACTURA</td>
                                            </tr>


                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>

                        <div class="list-group-item">
                            <div class="row">
                                <div class="col-sm-5">
                                    <h4 class="list-group-item-heading">Sistema de Emisi&oacute;n Electr&oacute;nica:
                                    </h4>
                                </div>
                                <div class="col-sm-7">


                                    <table class="table tblResultado">
                                        <tbody>



                                            <tr>
                                                <td>FACTURA PORTAL DESDE 23/03/2015</td>
                                            </tr>


                                            <tr>
                                                <td>BOLETA PORTAL DESDE 25/10/2018</td>
                                            </tr>


                                            <tr>
                                                <td>DESDE LOS SISTEMAS DEL CONTRIBUYENTE. AUTORIZ DESDE 27/10/2018</td>
                                            </tr>


                                            <tr>
                                                <td>GUIA DE REMISION DESDE 25/06/2021</td>
                                            </tr>


                                            <tr>
                                                <td>SEE-FACTURADOR . AUTORIZ DESDE 27/10/2018</td>
                                            </tr>

                                        </tbody>
                                    </table>

                                </div>
                            </div>
                        </div>

                        <div class="list-group-item">
                            <div class="row">
                                <div class="col-sm-5">
                                    <h4 class="list-group-item-heading">Emisor electr&oacute;nico desde:</h4>
                                </div>
                                <div class="col-sm-7">
                                    <p class="list-group-item-text">23/03/2015</p>
                                </div>
                            </div>
                        </div>

                        <div class="list-group-item">
                            <div class="row">
                                <div class="col-sm-5">
                                    <h4 class="list-group-item-heading">Comprobantes Electr&oacute;nicos:</h4>
                                </div>
                                <div class="col-sm-7">
                                    <p class="list-group-item-text">FACTURA (desde 23/03/2015),BOLETA (desde
                                        25/10/2018),GUIA (desde 25/06/2021)</p>
                                </div>
                            </div>
                        </div>

                        <div class="list-group-item">
                            <div class="row">
                                <div class="col-sm-5">
                                    <h4 class="list-group-item-heading">Afiliado al PLE desde:</h4>
                                </div>
                                <div class="col-sm-7">
                                    <p class="list-group-item-text">-</p>
                                </div>
                            </div>
                        </div>

                        <div class="list-group-item">
                            <div class="row">
                                <div class="col-sm-5">
                                    <h4 class="list-group-item-heading">Padrones:</h4>
                                </div>
                                <div class="col-sm-7">
                                    <table class="table tblResultado">
                                        <tbody>






                                            <!-- JRR - 20/09/2010 - Se añade cambio de Igor -->




                                            <tr>
                                                <td>NINGUNO</td>
                                            </tr>

                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>


                        <!-- <div class="list-group-item">
				    	<div class="row">
				    		<div class="col-sm-5">
	                         	<h4 class="list-group-item-heading">Razón Social:</h4>
	                         </div>
	                         <div class="col-sm-7">
	                         	<p class="list-group-item-text">eeee</p>
	                         </div>
				    	</div>  
			        </div> -->



                    </div><!-- fin list-group -->


                    <div class="panel-footer text-center">
                        <small>Fecha consulta: 05/03/2025 11:01</small>
                    </div><!-- fin footer del panel -->
                </div>
                <!--fin panel-->


                <div class="text-center divBotonera hidden-print">

                    <button type="button" class="btn btn-danger btnNuevaConsulta">Volver</button>

                </div>



                <div class="list-group-item hidden-print">
                    <div class="row">
                        <div class="col-sm-4">
                            <form name="forminfoHist" method="post" action="/cl-ti-itmrconsruc/jcrS00Alias">
                                <button type="button" class="btn btn-primary btn-sm btnInfHis" >Informaci&oacute;n Hist&oacute;rica</button>
                                <input type="hidden" name="accion" value="getinfHis">
                                <input type="hidden" name="contexto" id="contexto" value="ti-it">
                                <input type="hidden" name="modo" id="modo" value="1"><!-- modo móvil -->
                                <input type="hidden" name="nroRuc" value="20573215045">
                                <input type="hidden" name="desRuc" value="LEAD WORKING PARTNER SAC">
                            </form>
                        </div>
                        <div class="col-sm-4">
                            <form name="formInfoDeudaCoactiva" method="post" action="/cl-ti-itmrconsruc/jcrS00Alias">
                                <button type="button" class="btn btn-primary btn-sm btnInfDeuCoa"">Deuda Coactiva</button>          
			      <input type="hidden" name="accion" value="getInfoDC">
			      <input type="hidden" name="contexto" id="contexto" value="ti-it"> 				  		
		          <input type="hidden" name="modo" id="modo" value="1"><!-- modo móvil -->
			      <input type="hidden" name="nroRuc" value="20573215045">
			      <input type="hidden" name="desRuc" value="LEAD WORKING PARTNER SAC">
				</form>
            </div>
            <div class="col-sm-4">
            	<form name="formInfoOmisionTributaria" method="post" action="/cl-ti-itmrconsruc/jcrS00Alias">
			      <button type="button" class="btn btn-primary btn-sm btnInfOmiTri">Omisiones Tributarias</button>
                                <input type="hidden" name="accion" value="getInfoOT">
                                <input type="hidden" name="contexto" id="contexto" value="ti-it">
                                <input type="hidden" name="modo" id="modo" value="1"><!-- modo móvil -->
                                <input type="hidden" name="nroRuc" value="20573215045">
                                <input type="hidden" name="desRuc" value="LEAD WORKING PARTNER SAC">
                            </form>
                        </div>

                    </div>
                    <br>
                    <div class="row">
                        <div class="col-sm-4">
                            <form name="formNumTrabajd" method="post" action="/cl-ti-itmrconsruc/jcrS00Alias">
                                <button type="button" class="btn btn-primary btn-sm btnInfNumTra">Cantidad de Trabajadores y/o Prestadores de Servicio</button>
                                <input type="hidden" name="accion" value="getCantTrab">
                                <input type="hidden" name="contexto" id="contexto" value="ti-it">
                                <input type="hidden" name="modo" id="modo" value="1"><!-- modo móvil -->
                                <input type="hidden" name="nroRuc" value="20573215045">
                                <input type="hidden" name="desRuc" value="LEAD WORKING PARTNER SAC">
                            </form>
                        </div>

                        <div class="col-sm-4">
                            <form name="formActPro" method="post" action="/cl-ti-itmrconsruc/jcrS00Alias">
                                <button type="button" class="btn btn-primary btn-sm btnInfActPro">Actas Probatorias</button>
                                <input type="hidden" name="accion" value="getActPro">
                                <input type="hidden" name="contexto" id="contexto" value="ti-it">
                                <input type="hidden" name="modo" id="modo" value="1"><!-- modo móvil -->
                                <input type="hidden" name="desRuc" value="LEAD WORKING PARTNER SAC">
                                <input type="hidden" name="nroRuc" value="20573215045">
                            </form>
                        </div>


                        <div class="col-sm-4">
                            <form name="formActCPF" method="post" action="/cl-ti-itmrconsruc/jcrS00Alias">
                                <button type="button" class="btn btn-primary btn-sm btnInfActCPF">Facturas Fisicas</button>
                                <input type="hidden" name="accion" value="getActCPF">
                                <input type="hidden" name="contexto" id="contexto" value="ti-it">
                                <input type="hidden" name="modo" id="modo" value="1"><!-- modo móvil -->
                                <input type="hidden" name="desRuc" value="LEAD WORKING PARTNER SAC">
                                <input type="hidden" name="nroRuc" value="20573215045">
                            </form>

                        </div>

                    </div>


                    <br>
                    <div class="row">



                        <div class="col-sm-4">
                            <form name="formActReaPeru" method="post" action="/cl-ti-itmrconsruc/jcrS00Alias">
                                <button type="button" class="btn btn-primary btn-sm btnInfReaPer">Reactiva Perú : Deuda en cobranza coactiva</button>
                                <input type="hidden" name="accion" value="getReactivaPeru">
                                <input type="hidden" name="contexto" id="contexto" value="ti-it">
                                <input type="hidden" name="modo" id="modo" value="1"><!-- modo móvil -->
                                <input type="hidden" name="desRuc" value="LEAD WORKING PARTNER SAC">
                                <input type="hidden" name="nroRuc" value="20573215045">
                            </form>
                        </div>





                        <div class="col-sm-4">
                            <form name="formCovid" method="post" action="/cl-ti-itmrconsruc/jcrS00Alias">
                                <button type="button" class="btn btn-primary btn-sm btnInfCovid">Programa de garantías COVID_19 : Deuda en cobranza coactiva</button>
                                <input type="hidden" name="accion" value="getPGarantiaCOVID19">
                                <input type="hidden" name="contexto" id="contexto" value="ti-it">
                                <input type="hidden" name="modo" id="modo" value="1"><!-- modo móvil -->
                                <input type="hidden" name="desRuc" value="LEAD WORKING PARTNER SAC">
                                <input type="hidden" name="nroRuc" value="20573215045">
                            </form>
                        </div>




                        <div class="col-sm-4">
                            <form name="formRepLeg" method="post" action="/cl-ti-itmrconsruc/jcrS00Alias">
                                <button type="button" class="btn btn-primary btn-sm btnInfRepLeg">Representante(s) Legal(es)</button>
                                <input type="hidden" name="accion" value="getRepLeg">
                                <input type="hidden" name="contexto" id="contexto" value="ti-it">
                                <input type="hidden" name="modo" id="modo" value="1"><!-- modo móvil -->
                                <input type="hidden" name="desRuc" value="LEAD WORKING PARTNER SAC">
                                <input type="hidden" name="nroRuc" value="20573215045">
                            </form>
                        </div>

                    </div>
                    <br>
                    <div class="row">

                    </div>

                    <input type="hidden" name="accion" value="20573215045">
                    <input type="hidden" name="razSoc" value="LEAD WORKING PARTNER SAC">

                </div>

                <div class="list-group-item hidden-print">
                    <div class="row">
                        <div class="col-md-6 text-right">
                            <button class="btn btn-primary" onclick="imprimir()">
		    		<span class="glyphicon glyphicon-print"></span>
		    		Imprimir
		    	</button>
                        </div>

                        <div class="col-md-6">
                            <form class="form-inline" action="/cl-ti-itmrconsruc/jcrS00Alias" method="post"
                                name="formEnviar">
                                <!--inicio modificacion rmanriq1-->
                                <input type="hidden" name="nroRuc" value="20573215045">
                                <input type="hidden" name="desRuc" value="LEAD WORKING PARTNER SAC">
                                <!--fin modificacion rmanriq1-->
                                <input type="hidden" name="accion" value="enviar">
                                <input type="hidden" name="pagina" value="datosRuc" >
                                <input type="hidden" name="correo" value="">
                                <input type="email" name="email" required="required" placeholder="Ingresar Email">
                                <button type="submit" class="btn btn-primary" value="enviar">
						<span class="glyphicon glyphicon-envelope"></span>
						e-mail
					</button>
                            </form>

                        </div>

                    </div>
                </div>


            </div>
            <!--fin col-->
        </div>
        <!--fin row-->
        <footer class="footer text-center">
            <div class="col-md-12">
                <p><small>&copy; 1997 - 2025 SUNAT Derechos Reservados</small></p>
            </div>
        </footer>
    </div>
    <!--fin container-->


    <script src="/a/js/libs/jquery/1.11.1/jquery.min.js"></script>
    <script src="/a/js/libs/bootstrap/3.3.1/js/bootstrap.min.js"></script>
    <script>
        $( document ).ready(function() {
    		$.ajaxSetup({ scriptCharset: "utf-8" , contentType: "application/json; charset=utf-8"});
    	    jQuery.support.cors = true;

    	    iniciaVariables();
    	    iniciaBotones(); 
    	});
    	
   	 
    	/*Funciones usadas por la aplicación*/
    	function iniciaBotones(){
    	    //resetea estado inicial del formulario
    		$(".btnNuevaConsulta").bind('click',function(event){
    			regresa();

        		event.preventDefault();
        		event.stopImmediatePropagation();
        		return false;
        	});  
        	
        	
        	//resetea estado inicial del formulario
    		$(".btnInfHis").bind('click',function(event){
    			document.forminfoHist.submit();
        		event.preventDefault();
        		event.stopImmediatePropagation();
        		return false;
        	});
        	
        	//resetea estado inicial del formulario
    		$(".btnInfDeuCoa").bind('click',function(event){
    			document.formInfoDeudaCoactiva.submit();
        		event.preventDefault();
        		event.stopImmediatePropagation();
        		return false;
        	});  
        	
        	//resetea estado inicial del formulario
    		$(".btnInfOmiTri").bind('click',function(event){
    			document.formInfoOmisionTributaria.submit();
        		event.preventDefault();
        		event.stopImmediatePropagation();
        		return false;
        	});  
        	
        	//resetea estado inicial del formulario
    		$(".btnInfNumTra").bind('click',function(event){
    			document.formNumTrabajd.submit();
        		event.preventDefault();
        		event.stopImmediatePropagation();
        		return false;
        	});  
        	
        	//resetea estado inicial del formulario
    		$(".btnInfActPro").bind('click',function(event){
    			document.formActPro.submit();
        		event.preventDefault();
        		event.stopImmediatePropagation();
        		return false;
        	});  
        	
        	//resetea estado inicial del formulario
    		$(".btnInfActCPF").bind('click',function(event){
    			document.formActCPF.submit();
        		event.preventDefault();
        		event.stopImmediatePropagation();
        		return false;
        	});  
        	
        	//resetea estado inicial del formulario
    		$(".btnInfReaPer").bind('click',function(event){
    			document.formActReaPeru.submit();
        		event.preventDefault();
        		event.stopImmediatePropagation();
        		return false;
        	});  
        	
        	//resetea estado inicial del formulario
    		$(".btnInfCovid").bind('click',function(event){
    			document.formCovid.submit();
        		event.preventDefault();
        		event.stopImmediatePropagation();
        		return false;
        	});  
        	
        	//resetea estado inicial del formulario
    		$(".btnInfRepLeg").bind('click',function(event){
    			document.formRepLeg.submit();
        		event.preventDefault();
        		event.stopImmediatePropagation();
        		return false;
        	});  
        	
        	//resetea estado inicial del formulario
    		$(".btnInfLocAnex").bind('click',function(event){
    			document.formLocAnex.submit();
        		event.preventDefault();
        		event.stopImmediatePropagation();
        		return false;
        	});  
        	
        	
        	//resetea estado inicial del formulario
    	//(".btnPrueba").bind('click',function(event){
    	//		document.formPrueba.submit();
        //		event.preventDefault();
        //		event.stopImmediatePropagation();
        //		return false;
        	//});  
    	    
    	}

    	function regresa(){
    		
				var href = $("#aNuevaConsulta").attr('href');
	    		window.location.href = href;
			    		
    		
    	}
    	
    	function regresa1(){    	
    		document.forminfoHist.submit();
    		
    	}

    	function irHome(){
    		regresa();
    	}

    	/*Variables del app*/

    	
    	/*Declarar variables*/

    	/*Iniciar variables*/
    	function iniciaVariables(){
    	}
    	
    	
	  
    </script>



    <script src="/a/js/apps/workspace/ws.js"></script>


    <script type="text/javascript">
        function imprimir(){
    		window.print();
    	}
    
    </script>
</body>

</html>
"##;