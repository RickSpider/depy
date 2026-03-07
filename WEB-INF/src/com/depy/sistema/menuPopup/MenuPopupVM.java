package com.depy.sistema.menuPopup;


import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Window;




public class MenuPopupVM {
	
	@Init(superclass = true)
	public void initMenuPopupVM() {
		
	}

	@AfterCompose(superclass = true)
	public void afterComposeMenuPopupVM() {

	}
	
	private Window modal;
	
	@Command
	public void cambiarEmpresa() {
		

		modal = (Window) Executions.createComponents("/sistema/zul/menupopup/cambiarEmpresaModal.zul", null,
				null);

		Selectors.wireComponents(modal, this, false);
		modal.doModal();
	}

	@Command
	public void cambiarSucrusal() {
		

		modal = (Window) Executions.createComponents("/sistema/zul/menupopup/cambiarSucursalModal.zul", null,
				null);

		Selectors.wireComponents(modal, this, false);
		modal.doModal();
	}


	
	
	

}
