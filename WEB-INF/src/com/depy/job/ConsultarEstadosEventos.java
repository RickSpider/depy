package com.depy.job;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.depy.util.UtilLocalMetodos;
import com.doxacore.util.Register;




public class ConsultarEstadosEventos implements Job{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
	
		System.out.println("Iniciando consulta automatica de Eventos Factura...");
		UtilLocalMetodos ulm = new UtilLocalMetodos();
		ulm.updateEvento(new Register(), ulm.getSql("factura/listaFacturaEventoPendiente.sql"), ulm.getSql("factura/updateFacturaEvento.sql"));
		
		System.out.println("Iniciando consulta automatica de Eventos NotaCredito...");
		ulm = new UtilLocalMetodos();
		ulm.updateEvento(new Register(), ulm.getSql("notacredito/listaNotaCreditoEventoPendiente.sql"), ulm.getSql("notacredito/updateNotaCreditoEvento.sql"));
		
		System.out.println("Iniciando consulta automatica de Eventos Remision...");
		ulm = new UtilLocalMetodos();
		ulm.updateEvento(new Register(), ulm.getSql("remision/listaRemisionEventoPendiente.sql"), ulm.getSql("remision/updateRemisionEvento.sql"));
		
	}

}
