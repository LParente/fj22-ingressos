package br.com.caelum.ingresso.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.com.caelum.ingresso.dao.FilmeDao;
import br.com.caelum.ingresso.dao.SalaDao;
import br.com.caelum.ingresso.dao.SessaoDao;
import br.com.caelum.ingresso.model.Sessao;
import br.com.caelum.ingresso.model.form.SessaoForm;

@Controller
public class SessaoController {

	@Autowired
	private SalaDao salaDao;
	@Autowired
	private FilmeDao filmeDao;

	@Autowired
	private SessaoDao sessaoDao;
	
	@GetMapping("/admin/sessao")
	private ModelAndView form(@RequestParam("salaId") Integer salaId, SessaoForm formulario) {
		
		formulario.setSalaId(salaId);
		
		ModelAndView mav = new ModelAndView("sessao/sessao");

		mav.addObject("sala", salaDao.findOne(salaId));
		mav.addObject("filme", filmeDao.findAll());
		mav.addObject("formulario", formulario);
		
		return mav;
	}
	
	private ModelAndView salva(@Valid SessaoForm form, BindingResult result) {
		if(result.hasErrors()) 
			return form(form.getSalaId(), form);
		
		Sessao sessao = form.toSessao(filmeDao, salaDao);
		sessaoDao.save(sessao);
		
		return new ModelAndView("redirect:/admin/sala/" + form.getSalaId() + "/sessoes");
		
	}
}

