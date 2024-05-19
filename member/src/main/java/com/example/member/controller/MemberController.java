package com.example.member.controller;

import com.example.member.dto.MemberDTO;
import com.example.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Member;
import java.util.List;

@Tag(name = "Member", description = "Member 관련된 API 입니다.")
@Controller
@RequiredArgsConstructor // 생성자 만들어주는
public class MemberController {
    // 생성자 주입
    private final MemberService memberService;

    // 회원 가입 페이지 출력 요청
    @GetMapping("/member/save")
    public String saveForm() {
        return "save";
    }

    @Operation(summary = "회원가입", description = "회원가입을 합니다.")
    @ApiResponse(responseCode = "200", description = "회원가입에 성공하였습니다.")
    @PostMapping("/member/save")
    public String save(@ModelAttribute MemberDTO memberDTO) { // @RequestParam("memberEmail") 처럼 요소만 가져올 수도 있음.
        System.out.println("MemberController.save"); // soutm
        System.out.println("memberDTO = " + memberDTO); // soutp
        memberService.save(memberDTO);
        return "login";
    }

    @GetMapping("/member/login")
    public String loginForm() {
        return "login";
    }

    @Operation(summary = "로그인", description = "로그인 합니다.")
    @ApiResponse(responseCode = "200", description = "로그인에 성공하였습니다.")
    @PostMapping("/member/login")
    public String login(@ModelAttribute MemberDTO memberDTO, HttpSession session) {
        MemberDTO loginResult = memberService.login(memberDTO);
        if (loginResult != null) {
            // login 성공
            session.setAttribute("loginEmail", loginResult.getMemberEmail());
            return "main";
        } else {
            // login 실패
            // 실패시 실패코드를 넘겨주는 방법 필요
            // 프론트 코드를 무시한다면 ResponseEntity<>로 반환해주어야 API의 기능이라고 볼 수 있음. 현재는 프론트까지 같이 고려하므로 적용 애매
            return "login";
        }
    }

//    @Operation(summary = "로그인", description = "로그인 합니다.")
//    @ApiResponse(responseCode = "200", description = "로그인에 성공하였습니다.")
//    @ApiResponse(responseCode = "401", description = "로그인에 실패하였습니다.")
//    @PostMapping("/member/login")
//    public ResponseEntity<String> login(@ModelAttribute MemberDTO memberDTO, HttpSession session) {
//        MemberDTO loginResult = memberService.login(memberDTO);
//        if (loginResult != null) {
//            // login 성공
//            session.setAttribute("loginEmail", loginResult.getMemberEmail());
//            return ResponseEntity.ok("main");
//        } else {
//            // login 실패
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("login");
//        }
//    }

    @GetMapping("/member/")
    public String findAll(Model model) {
        List<MemberDTO> memberDTOList = memberService.findAll();
        // 어떠한 html로 가져갈 데이터가 있다면 model로 정보전달
        model.addAttribute("memberList", memberDTOList);
        return "list";
    }

    @GetMapping("/member/{id}")
    public String findById(@PathVariable("id") Long id, Model model) { // {id} 값을 가져오기 위해 @PathVariable 어노테이션 사용
        MemberDTO memberDTO = memberService.findById(id);
        model.addAttribute("member", memberDTO); // detail.html에 전달할 정보
        return "detail";
    }

    @GetMapping("/member/update")
    public String updateForm(HttpSession session, Model model) {
        String myEmail = (String) session.getAttribute("loginEmail"); // 형변환
        MemberDTO memberDTO = memberService.updateForm(myEmail);
        model.addAttribute("updateMember", memberDTO);
        return "update";
    }

    @PostMapping("/member/update")
    public String update(@ModelAttribute MemberDTO memberDTO) {
        memberService.update(memberDTO);
        return "redirect:/member/" + memberDTO.getId();
    }

    @GetMapping("/member/delete/{id}")
    public String deleteById(@PathVariable("id") Long id) {
        memberService.deleteById(id);
        return "redirect:/member/";
    }

    @GetMapping("/member/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "index";
    }

    @PostMapping("/member/email-check")
    // ajax 요청을 받을 땐 @ResponseBody 어노테이션을 붙여줘야함.
    public @ResponseBody String emailCheck(@RequestParam("memberEmail") String memberEmail) {
        System.out.println("memberEmail = " + memberEmail);
        String checkResult = memberService.emailCheck(memberEmail);
        return checkResult;
    }
}
