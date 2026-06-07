package kr.ac.hansung.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.dto.PasswordChangeDto;
import kr.ac.hansung.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/user/password")
    public String passwordForm(Model model){
        model.addAttribute("passwordChangeDto", new PasswordChangeDto());
        return "user/password";
    }

    @PostMapping("/user/password")
    public String changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute PasswordChangeDto dto,
            BindingResult bindingResult,
            RedirectAttributes ra) {

        // 1. dto 자체의 validation(@Valid)에러나 비밀번호 불일치 에러를 먼저 체크
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "새 비밀번호가 일치하지 않습니다");
        }

        // ⭐ [추가/위치 변경] 비밀번호가 일치하지 않거나, @Valid 검증을 통과하지 못했다면 바로 리턴!
        if (bindingResult.hasErrors()) {
            return "user/password";
        }

        try {
            // 2. 위에서 에러가 없을 때만 안전하게 서비스 로직 호출
            userService.changePassword(userDetails.getUsername(),
                    dto.getCurrentPassword(), dto.getNewPassword());

            ra.addFlashAttribute("successMessage", "비밀번호가 변경되었습니다");
            return "redirect:/home";

        } catch (IllegalArgumentException e) {
            // 현재 비밀번호가 틀린 경우 예외 처리
            bindingResult.rejectValue("currentPassword", "wrong", e.getMessage());
            return "user/password"; // 굳이 아래로 내려가지 않고 여기서 바로 리턴하는 것이 깔끔합니다.
        }
    }
}