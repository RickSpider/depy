package com.depy.util;

import com.doxacore.util.Params;

public class ParamsLocal extends Params{


	public static final String ROL_OPERADOR = "Operador";
	public static final String ROL_ADMIN="Admin";
	
	public static final String SIGLA_TIPOTIPO_DE = "DE";
	public static final String SIGLA_TIPO_DE_FE = "FACTURA_ELECTRONICA";
	public static final String SIGLA_TIPO_DE_NC = "NOTA_CREDITO";
	public static final String SIGLA_TIPO_DE_ND = "NOTA_DEBITO";
	public static final String SIGLA_TIPO_DE_RE = "REMISION";
	
	/*
	EJEMPLO DE COMO CREAR PARAMETROS 
		 
	public static final String OP_CREAR_? = "Crear?";
	public static final String OP_EDITAR_? = "Editar?";
	public static final String OP_BORRAR_? = "Borrar?";
	
	 */
	
	public static final String OP_CREAR_EMPRESA = "CrearEmpresa";
	public static final String OP_EDITAR_EMPRESA = "EditarEmpresa";
	public static final String OP_BORRAR_EMPRESA = "BorrarEmpresa";
	
	
	public static final String OP_CREAR_EMPRESAUSUARIO = "CrearEmpresaUsuario";
	public static final String OP_EDITAR_EMPRESAUSUARIO = "EditarEmpresaUsuario";
	public static final String OP_BORRAR_EMPRESAUSUARIO = "BorrarEmpresaUsuario";
	
	public static final String OP_CREAR_SUCURSAL = "CrearSucursal";
	public static final String OP_EDITAR_SUCURSAL = "EditarSucursal";
	public static final String OP_BORRAR_SUCURSAL = "BorrarSucursal";
	
	public static final String OP_CREAR_CLIENTE = "CrearCliente";
	public static final String OP_EDITAR_CLIENTE = "EditarCliente";
	public static final String OP_BORRAR_CLIENTE = "BorrarCliente";
	
	public static final String OP_CREAR_FACTURA = "CrearFactura";
	public static final String OP_EDITAR_FACTURA = "EditarFactura";
	public static final String OP_BORRAR_FACTURA = "BorrarFactura";
	
	public static final String OP_CREAR_FE = "CrearFe";
	public static final String OP_EDITAR_FE = "EditarFe";
	public static final String OP_BORRAR_FE = "BorrarFe";

	
	public static final String OP_CREAR_COMPROBANTE = "CrearComprobante";
	public static final String OP_EDITAR_COMPROBANTE = "EditarComprobante";
	public static final String OP_BORRAR_COMPROBANTE = "BorrarComprobante";
	
	public static final String SIGLA_TIPOTIPO_CONDICIONPAGO= "CONDICIONPAGO";
	public static final String SIGLA_TIPO_CONDICIONPAGO_CONTADO = "CONDICIONPAGO_CONTADO";
	public static final String SIGLA_TIPO_CONDICIONPAGO_CREDITO = "CONDICIONPAGO_CREDITO";
	
	public static final String SIGLA_TIPOTIPO_FORMAPAGO= "FORMAPAGO";
	public static final String SIGLA_TIPO_FORMAPAGO_EFECTIVO = "FORMAPAGO_EFECTIVO";

	
	public static final String SIGLA_TIPOTIPO_MONEDA = "MONEDA";
	public static final String SIGLA_TIPO_MONEDA_GUARANIES = "MONEDA_GUARANIES";
	public static final String SIGLA_TIPO_MONEDA_DOLARES = "MONEDA_DOLARES";
	
	public static final String SIGLA_TIPOTIPO_IVA = "IVA";
	public static final String SIGLA_TIPO_IVA_10 = "IVA_10";
	public static final String SIGLA_TIPO_IVA_5 = "IVA_5";
	public static final String SIGLA_TIPO_IVA_0 = "IVA_0";
	
	public static final String SIGLA_TIPOTIPO_DOCUMENTO = "DOCUMENTO";
	public static final String SIGLA_TIPO_DOCUMENTO_RUC = "DOCUMENTO_RUC";
	public static final String SIGLA_TIPO_DOCUMENTO_CI = "DOCUMENTO_CI";
	public static final String SIGLA_TIPO_DOCUMENTO_PASAPORTE = "DOCUMENTO_PASAPORTE";
	public static final String SIGLA_TIPO_DOCUMENTO_CE = "DOCUMENTO_CE";
	public static final String SIGLA_TIPO_DOCUMENTO_CR = "DOCUMENTO_CR";
	
	public static final String SIGLA_TIPOTIPO_COMPROBANTE= "COMPROBANTE";
	public static final String SIGLA_TIPO_COMPROBANTE_FACTURA = "COMPROBANTE_FACTURA";
	public static final String SIGLA_TIPO_COMPROBANTE_NOTACREDITO = "COMPROBANTE_NOTACREDITO";
	public static final String SIGLA_TIPO_COMPROBANTE_REMISION = "COMPROBANTE_REMISION";
	public static final String SIGLA_TIPO_COMPROBANTE_NOTADEBITO = "COMPROBANTE_NOTADEBITO";
	public static final String SIGLA_TIPO_COMPROBANTE_AUTOFACTURA = "COMPROBANTE_AUTOFACTURA";
	
	public static final String SIGLA_TIPOTIPO_EVENTO= "EVENTO";
	public static final String SIGLA_TIPO_EVENTO_CANCELACION = "EVENTO_CANCELACION";
	public static final String SIGLA_TIPO_EVENTO_INUTILIZACION = "EVENTO_INUTILIZACION";
	
	public static final String SIGLA_TIPOTIPO_MOTIVONCD= "MOTIVONCD";
	public static final String SIGLA_TIPO_MOTIVONCD_DEVOLUCIONAJUSTEPRECIO = "MOTIVONCD_DEVOLUCIONAJUSTEPRECIO";
	public static final String SIGLA_TIPO_MOTIVONCD_DEVOLUCION = "MOTIVONCD_DEVOLUCION";
	public static final String SIGLA_TIPO_MOTIVONCD_DESCUENTO = "MOTIVONCD_DESCUENTO";
	public static final String SIGLA_TIPO_MOTIVONCD_BONIFICACION = "MOTIVONCD_BONIFICACION";
	public static final String SIGLA_TIPO_MOTIVONCD_CREDITOINCOBRABLE = "MOTIVONCD_CREDITOINCOBRABLE";
	public static final String SIGLA_TIPO_MOTIVONCD_RECUPEROCOSTO = "MOTIVONCD_RECUPEROCOSTO";
	public static final String SIGLA_TIPO_MOTIVONCD_RECUPEROGASTO = "MOTIVONCD_RECUPEROGASTO";
	public static final String SIGLA_TIPO_MOTIVONCD_AJUSTOPRECIO = "MOTIVONCD_AJUSTOPRECIO";
	
	public static final String SIGLA_TIPOTIPO_MOTIVOREMISION= "MOTIVOREMISION";
	public static final String SIGLA_TIPO_MOTIVOREMISION_TRASLADOVENTA = "MOTIVOREMISION_TRASLADOVENTA";
	
	public static final String SIGLA_TIPOTIPO_TRANSPORTE= "TRANSPORTE";
	public static final String SIGLA_TIPO_TRANSPORTE_PROPIO = "TRANSPORTE_PROPIO";
	public static final String SIGLA_TIPO_TRANSPORTE_TERCERO = "TRANSPORTE_TERCERO";
	
	public static final String SIGLA_TIPOTIPO_TRANSPORTEMODALIDAD= "TRANSPORTEMODALIDAD";
	public static final String SIGLA_TIPO_TRANSPORTEMODALIDAD_TERRESTRE = "TRANSPORTEMODALIDAD_TERRESTRE";
	public static final String SIGLA_TIPO_TRANSPORTEMODALIDAD_FLUVIAL = "TRANSPORTEMODALIDAD_FLUVIAL";
	public static final String SIGLA_TIPO_TRANSPORTEMODALIDAD_AEREO = "TRANSPORTEMODALIDAD_AEREO";
	public static final String SIGLA_TIPO_TRANSPORTEMODALIDAD_MUTIMODAL = "TRANSPORTEMODALIDAD_MUTIMODAL";
	
	public static final String SIGLA_TIPOTIPO_RESPONSABLEREMI= "RESPONSABLEREMI";
	public static final String SIGLA_TIPO_RESPONSABLEREMI_EMISORFE = "RESPONSABLEREMI_EMISORFE";
	
	
	public static final String SIGLA_TIPOTIPO_RESPONSABLEFLETE= "RESPONSABLEFLETE";
	public static final String SIGLA_TIPO_RESPONSABLEFLETE_EMISORFE = "RESPONSABLEFLETE_EMISORFE";
	
	public static final String SIGLA_TIPOTIPO_VEHICULOIDENTIFICACION= "VEHICULOIDENTIFICACION";
	public static final String SIGLA_TIPO_VEHICULOIDENTIFICACION_NROIDENT = "VEHICULOIDENTIFICACION_NROIDENT";
}
