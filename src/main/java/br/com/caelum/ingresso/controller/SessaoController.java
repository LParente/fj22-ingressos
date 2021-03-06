package br.com.caelum.ingresso.controller;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.com.caelum.ingresso.dao.FilmeDao;
import br.com.caelum.ingresso.dao.SalaDao;
import br.com.caelum.ingresso.dao.SessaoDao;
import br.com.caelum.ingresso.model.Carrinho;
import br.com.caelum.ingresso.model.ImagemCapa;
import br.com.caelum.ingresso.model.Sala;
import br.com.caelum.ingresso.model.Sessao;
import br.com.caelum.ingresso.model.TipoDeIngresso;
import br.com.caelum.ingresso.model.form.SessaoForm;
import br.com.caelum.ingresso.rest.ImdbClient;
import br.com.caelum.ingresso.validacao.GerenciadorDeSessao;

@Controller
public class SessaoController {

	@Autowired
	private SalaDao salaDao;
	
	@Autowired
	private FilmeDao filmeDao;

	@Autowired
	private SessaoDao sessaoDao;
	
	private EntityManager manager;

	@Autowired
	private ImdbClient client;
	
	@Autowired
	private Carrinho carrinho;
	
	@GetMapping("/admin/sessao")
	public ModelAndView form(@RequestParam("salaId") Integer salaId, SessaoForm formulario) {
		
		formulario.setSalaId(salaId);
		
		ModelAndView mav = new ModelAndView("sessao/sessao");
		System.out.println(salaId);
		System.out.println(salaDao);
		Sala sala = salaDao.findOne(salaId);
		mav.addObject("sala", sala);
		mav.addObject("filmes", filmeDao.findAll());
		mav.addObject("form", formulario);
		
		return mav;
	}
	
	
	@PostMapping(value = "/admin/sessao")
	@Transactional
	public ModelAndView salva(@Valid SessaoForm form, BindingResult result) {
		if(result.hasErrors()) 
			return form(form.getSalaId(), form);
		
		Sessao sessao = form.toSessao(filmeDao, salaDao);
		
		List<Sessao> sessoesDaSala = sessaoDao.buscaSessoesDaSala(sessao.getSala());
		
		GerenciadorDeSessao gerenciador = new GerenciadorDeSessao(sessoesDaSala);
		
		if (gerenciador.cabe(sessao)) {
			sessaoDao.save(sessao);
			return new ModelAndView("redirect:/admin/sala/" + form.getSalaId() + "/sessoes");
		}
		return form(form.getSalaId(), form);
	}
	
	@GetMapping("/sessao/{id}/lugares")
	public ModelAndView lugaresNaSessao(@PathVariable("id") Integer sessaoId){
		ModelAndView mav = new ModelAndView("sessao/lugares");
		
		Sessao sessao = sessaoDao.findOne(sessaoId);
		
		Optional<ImagemCapa> imagemCapa = client.request(sessao.getFilme(), ImagemCapa.class);
		
		mav.addObject("sessao", sessao);
		mav.addObject("carrinho", carrinho);
		mav.addObject("imagemCapa", imagemCapa.orElse(new ImagemCapa()));
		mav.addObject("tiposDeIngressos", TipoDeIngresso.values());
		
		return mav;
	}
}

