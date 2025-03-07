package local.host.trader.frontend.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import local.host.trader.frontend.Application;
import local.host.trader.frontend.model.TraderUser;
import local.host.trader.frontend.model.Session;
import local.host.trader.frontend.repository.PublisherRepository;
import local.host.trader.frontend.service.CurrentUser;
import local.host.trader.frontend.service.SessionService;

@Controller
public class PublisherController {

	@Autowired
	private PublisherRepository publisherRepository;

	@Autowired
	private SessionService sessionService;

	@RequestMapping(method = RequestMethod.GET, value = "/trader/trade")
	public String provideUploadInfo(Model model) {
		return "trader/trade";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/trader/trade")
	@PreAuthorize("hasRole('TRADER')")
	public String handleFileUpload(@RequestParam("name") String name, @RequestParam("exchange")Long exchangeId, @RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes, @AuthenticationPrincipal Principal principal) {

		CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
		Optional<TraderUser> publisher = publisherRepository.findByUser(activeUser.getUser());

		String uuid = UUID.randomUUID().toString();
		File dir = new File(getDirectory(publisher.get().getId()));
		createDirectoryIfNotExist(dir);

		File f = new File(getFileName(publisher.get().getId(), uuid));
		if (!file.isEmpty()) {
			try {
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f));
				FileCopyUtils.copy(file.getInputStream(), stream);
				stream.close();
				Session session = new Session();
				session.setUuid(uuid);
				session.setName(name);
				sessionService.publish(publisher.get(), session, exchangeId);
				return "redirect:/trader/browse";
			} catch (Exception e) {
				redirectAttributes.addFlashAttribute("message",
						"You failed to publish " + name + " => " + e.getMessage());
			}
		} else {
			redirectAttributes.addFlashAttribute("message",
					"You failed to upload " + name + " because the file was empty");
		}

		return "redirect:/trader/trade";
	}

	private boolean createDirectoryIfNotExist(File dir) {
		if (!dir.exists()) {
			boolean created = dir.mkdirs();
			if (!created) {
				return false;
			}
		}
		return true;
	}

	public static String getFileName(long publisherId, String uuid) {
		return getDirectory(publisherId) + "/" + uuid + ".pdf";
	}

	public static String getDirectory(long publisherId) {
		return Application.ROOT + "/" + publisherId;
	}

}