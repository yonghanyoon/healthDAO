package com.kh.healthDao.admin;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.healthDao.admin.model.service.AdminService;
import com.kh.healthDao.admin.model.service.CouponService;
import com.kh.healthDao.admin.model.service.MemberSoundService;
import com.kh.healthDao.admin.model.service.NoticeService;
import com.kh.healthDao.admin.model.vo.Coupon;
import com.kh.healthDao.admin.model.vo.Notice;
import com.kh.healthDao.admin.model.vo.Product;
import com.kh.healthDao.member.model.vo.Member;
import com.kh.healthDao.member.model.vo.UserImpl;
import com.kh.healthDao.mypage.model.vo.MemberSound;

@Controller
@RequestMapping("/admin/*")
public class adminController {
	
	private AdminService adminService;
	private CouponService couponService;
	private NoticeService noticeService;
	private MemberSoundService memberSoundService;
	
	@Autowired
	public adminController(AdminService adminService, CouponService couponService, NoticeService noticeService,  MemberSoundService memberSoundService) {
		this.adminService = adminService;
		this.couponService = couponService;
		this.noticeService = noticeService;
		this.memberSoundService = memberSoundService;
	}
	
	@GetMapping(value= {"/", "/myOrder"})
	public String mypage() {
		return "redirect:/admin/memberInfo";
	}
	
	@GetMapping("/productRegist")
	public String registMain() {
		return "admin/adminProductRegist";
	}
	
	/* 상품 등록 */
	@PostMapping("productRegist")
	@ResponseBody
	public ModelAndView registProduct(Product product, ModelAndView mv) {
		/* List<Option> options = new ArrayList<>();
		options.add(option);
		
		product.setOption(options);
		product.setCategory(category); */
		
		System.out.println(product);
		int result = adminService.registProduct(product);
		//int result2 = adminService.registCategory(product);
		int result3 = adminService.registOption(product);
		if(result > 0) {
			mv.setViewName("redirect:/admin/productRegist");
			return mv;
			
		}else {
			mv.setViewName("redirect:/ProductRegist");
			return mv;
		}
	}

	

	// 페이징 된 리스트
	@GetMapping("inventoryList")
	public ModelAndView managerInventoryList(ModelAndView mv, @RequestParam int page) {

		Map<String, Object> map = adminService.inventoryPaging(page);
		
		mv.addObject("ProductList", map.get("ProductList"));
		mv.addObject("listCount", map.get("listCount"));
		mv.addObject("pi", map.get("pi"));
		mv.setViewName("admin/inventoryList");

		return mv;
	}
	
	// 팝업 수량 입력
	@PostMapping("/pLPopupSu")
	@ResponseBody
	public ModelAndView pLPopupSu(Product product, ModelAndView mv, RedirectAttributes rttr) {
		// System.out.println(product.getProductStock());
		// System.out.println(product.getProductNo());
		
		String msg = adminService.stockPlus(product) > 0 ? "답변 등록 성공" : "답변 등록 실패";
		
		rttr.addFlashAttribute("msg", msg);
			
		mv.setViewName("redirect:/admin/inventoryList?page=1");
		return mv;
	}
	
	// 쿠폰등록
	@GetMapping("/couponInput")
	public String couponInput() {
		return "admin/couponInput";
	}
	
	@PostMapping("/couponInput")
	public String couponInput(Coupon coupon, RedirectAttributes rttr) {
		String msg = couponService.couponInput(coupon) > 0 ? "쿠폰 등록 성공" : "쿠폰 등록 실패";
		
		rttr.addFlashAttribute("msg", msg);
		
		return "redirect:/admin/couponList";
	}
	
	// 쿠폰리스트
	@GetMapping("/couponList")
	public ModelAndView couponList(ModelAndView mv, @RequestParam int page) {
		Map<String, Object> map = couponService.allCouponList(page);
		
		mv.addObject("couponList", map.get("couponList"));
		mv.addObject("listCount", map.get("listCount"));
		mv.addObject("pi", map.get("pi"));
		mv.setViewName("admin/couponList");
		
		return mv;
	}
	
	// 공지사항 리스트
	@GetMapping("/noticeList")
	public ModelAndView noticeList(ModelAndView mv, @RequestParam int page) {
		Map<String, Object> map = noticeService.allNoticeList(page);
		
		mv.addObject("noticeList", map.get("noticeList"));
		mv.addObject("listCount", map.get("listCount"));
		mv.addObject("pi", map.get("pi"));
		mv.setViewName("admin/noticeList");
		
		return mv;
	}
	
	// 공지사항 등록
	@GetMapping("/noticeInsert")
	public String noticeInsert() {
		return "admin/noticeInput";
	}
	
	@PostMapping("/noticeInsert")
	public String noticeInsert(Notice notice, RedirectAttributes rttr, @AuthenticationPrincipal UserImpl userImpl) {
		int userNo = userImpl.getUserNo();
		notice.setUserNo(userNo);
		
		String msg = noticeService.noticeInsert(notice) > 0 ? "공지사항 등록 완료" : "공지사항 등록 실패";
		
		rttr.addFlashAttribute("msg", msg);
		
		return "redirect:/admin/noticeList?page=1";
	}
	
	// 공지사항 상세
	@GetMapping("/noticeDetail")
	public ModelAndView noticeDetail(ModelAndView mv, @RequestParam int nNo) {
		
		Notice notice = noticeService.noticeDetail(nNo);
		
		mv.addObject("notice", notice);
		mv.setViewName("admin/noticeDetail");
		
		return mv;
	}
	
	@PostMapping("/admin/noticeModify")
	public String noticeModify(Notice notice, RedirectAttributes rttr) {
		
		String msg = noticeService.noticeModify(notice) > 0 ? "공지사항 수정 완료" : "공지사항 수정 실패";
		
		rttr.addFlashAttribute("msg", msg);
		
		return "redirect:/admin/noticeList?page=1";
	}
	
	@GetMapping("/memberSoundList")
	public ModelAndView memberSoundList(ModelAndView mv, @RequestParam int page) {
		Map<String, Object> map = memberSoundService.memberSoundList(page);
		
		mv.addObject("memberSoundList", map.get("memberSoundList"));
		mv.addObject("listCount", map.get("listCount"));
		mv.addObject("pi", map.get("pi"));
		mv.setViewName("admin/memberSoundList");
		
		return mv;
	}
	
	@PostMapping("memberSoundDetail")
	@ResponseBody
	public MemberSound memberSoundDetail(int cNo) {
		MemberSound memberSound  = memberSoundService.memberSoundDetail(cNo);
		
		return memberSound;
	}
	
	@PostMapping("memberSoundModify")
	public String memberSoundModify(MemberSound ms, RedirectAttributes rttr) {
		String msg = memberSoundService.memberSoundModify(ms) > 0 ? "진행상황 적용 완료" : "진행상황 적용 실패";
		
		rttr.addFlashAttribute("msg", msg);
		
		return "redirect:/admin/memberSoundList?page=1";
	}
	
	/* 회원정보 관리 */
	@GetMapping("memberInfo")
	public ModelAndView memberInfoView(ModelAndView mv) {
		List<Member> memberList = adminService.memberInfoView();
		
		mv.addObject("memberList", memberList);
		mv.setViewName("admin/memberInfo");
		
		return mv;
	}
	
	@GetMapping("trainerInfo")
	public ModelAndView trainerInfoView(ModelAndView mv) {
		List<Member> trainerList = adminService.trainerInfoView();
		
		mv.addObject("trainerList", trainerList);
		mv.setViewName("admin/trainerInfo");
		
		return mv;
	}
	
}
