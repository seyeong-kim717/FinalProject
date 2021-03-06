package com.sinbal.spring.product.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.sinbal.spring.product.dao.ProductDao;
import com.sinbal.spring.product.dto.ProductDto;
import com.sinbal.spring.shop.dto.ShopDto;

@Service
public class ProductServiceImpl implements ProductService{
	@Autowired
	private ProductDao productDao;
	
	@Transactional
	@Override
	public void insert(ProductDto dto ,HttpServletRequest request) {
		productDao.insert(dto);
		int num=productDao.getnum();
		String[] sbsize= request.getParameterValues("sizearr");
		String[] sbcount= request.getParameterValues("sbcount");
		for(int i=0; i<sbsize.length;i++) {
			dto.setSbsize(Integer.parseInt(sbsize[i]));
			dto.setSbcount(Integer.parseInt(sbcount[i]));
			dto.setNum(num);
			productDao.insert_sub(dto);
		}
		
		
		
		
	}
	@Override
	public Map<String, Object> saveProfileImage(HttpServletRequest request, MultipartFile mFile) {
		//원본 파일명
		String orgFileName=mFile.getOriginalFilename();
		// webapp/upload 폴더 까지의 실제 경로(서버의 파일시스템 상에서의 경로)
		String realPath=request.getServletContext().getRealPath("/upload");
		//저장할 파일의 상세 경로
		String filePath=realPath+File.separator;
		//디렉토리를 만들 파일 객체 생성
		File upload=new File(filePath);
		if(!upload.exists()) {//만일 디렉토리가 존재하지 않으면 
			upload.mkdir(); //만들어 준다.
		}
		//저장할 파일 명을 구성한다.
		String saveFileName=
				System.currentTimeMillis()+orgFileName;
		try {
			//upload 폴더에 파일을 저장한다.
			mFile.transferTo(new File(filePath+saveFileName));
			System.out.println(filePath+saveFileName);
		}catch(Exception e) {
			e.printStackTrace();
		}
		//Map 에 업로드된 이미지 파일의 경로를 담아서 리턴한다
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("imageSrc","/upload/"+saveFileName);
		
		return map;
	}
	@Override
	public void getList(ModelAndView mView) {
		
		
		mView.addObject("list",productDao.getList());
		
		
	}
	//신발 사이즈와 수량을 추가
	@Override
	public void insert_sub(ProductDto dto) {

	}
	@Override
	public Map<String, Object> isExistproductname(String inputproductname) {
		boolean isExist = productDao.isExist(inputproductname);
		//아이디가 존재하는 지 여부를 Map 에 담아서 리턴해준다.
		Map<String, Object> map = new HashMap<>();
		map.put("isExist", isExist);
		return map;
		
	}
	@Override
	public void productdelete(int num) {
		productDao.productdelete(num);
		
	}
	@Override
	public void getData(ModelAndView mView ,int num) {
		List<ProductDto> list=productDao.getData(num);
		mView.addObject("list",list);
	}
	
	@Transactional
	@Override
	public void productupdate(ModelAndView mView, ProductDto dto ,HttpServletRequest request) {
		productDao.productupdate(dto);
		String[] sbsize= request.getParameterValues("sizearr");
		String[] sbcount= request.getParameterValues("sbcount");
		
		for(int i=0; i<sbsize.length;i++) {
			dto.setSbsize(Integer.parseInt(sbsize[i]));
			dto.setSbcount(Integer.parseInt(sbcount[i]));
			productDao.productupdate_sub(dto);
		}
		
	}
	//상품 번호에 맞는 상품 정보를 가져오는 추상 메소드
	@Override
	public void getProductData(ModelAndView mView, int num) {
		//상품 정보를 가져온다.
		ProductDto dto = productDao.getData2(num);
		//신발 사이즈, 신발 수량 정보를 가져온다.
		List<ProductDto> dto_sub = productDao.getSubData(num);
		//mView에 담는다.
		mView.addObject("productDto", dto);
		mView.addObject("productDto_sub", dto_sub);
	}
	//특정 사이즈의 재고 개수를 리턴하는 메소드
	@Override
	public ProductDto getStockData(int size, int num) {
		//재고 개수를 가져온다.
		ProductDto dto = productDao.getStockData(size, num);
		return dto;
	}
	//선택할 수 있는 신발 사이즈 항목의 개수를 리턴하는 메소드
	@Override
	public int getSizeData(int num) {
		//재고 개수를 가져온다.
		int number = productDao.getSizeData(num);
		return number;
	}
	//선택한 신발 사이즈에 해당하는 가격을 가져오는 추상 메소드
	@Override
	public Map<String, Object> getSbsizePrice(int size, int num) {
		//특정 상품번호에 대한 정보를 가져온다.
		List<ProductDto> list_dto = productDao.getSubData(num);
		//특정 상품번호의 특정 신발 사이즈에 대한 정보를 가져온다.
		ProductDto dto = productDao.getStockData(size, num);
		//선택한 신발 사이즈에 대한 총 가격을 구한다.
		//총 가격을 저장할 변수 선언
		int totalPrice = 0;
		for(int i=0; i<list_dto.size(); i++) {
			totalPrice = totalPrice + list_dto.get(i).getPrice();
		}
		//특정 신발 사이즈에 맞는 가격을 가져온다.
		int price = dto.getPrice();
		//Map 객체에 정보를 담는다.
		Map<String, Object> priceInfo = new HashMap<>();
		priceInfo.put("totalPrice", totalPrice);
		priceInfo.put("price", price);
		return priceInfo;
	}
	
}
