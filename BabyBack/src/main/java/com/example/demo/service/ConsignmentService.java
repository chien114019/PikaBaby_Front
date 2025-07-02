package com.example.demo.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ConsignDTO;
import com.example.demo.model.Consignment;
import com.example.demo.model.Customer;
import com.example.demo.model.ProductType;
import com.example.demo.model.Response;
import com.example.demo.repository.ConsignmentRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ProductTypeRepository;

@Service
public class ConsignmentService {

	@Autowired
	private ConsignmentRepository repository;

	@Autowired
	private ProductTypeRepository ptRepository;

	@Autowired
	private CustomerRepository cRepository;

	ProductType pType;
	Customer cust;

	public List<Consignment> getAll() {
		return repository.findAll();
	}

	public Consignment getById(String id) {
		Consignment consignment = repository.findById(Integer.parseInt(id)).orElse(null);
		System.out.println(consignment);
		return consignment;
	}

//	============= 前台API ===============

	public List<Consignment> getAllByCustId(String custId) {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		List<Consignment> consignments = repository.findAllByCustomer(cust);
		return consignments;
	}

//	getAllByDateAndTypeAndReview
	public List<Consignment> getAllByCustIdAndDateAndTypeAndReview(String custId, String applyStart, String applyEnd,
			String type, String review) throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		pType = ptRepository.findById(Integer.parseInt(type)).orElse(null);
		List<Consignment> consignments = repository.findAllByCustomerAndApplyDateBetweenAndProductTypeAndReview(cust,
				formatter(applyStart), formatter(applyEnd), pType, Integer.parseInt(review));
		return consignments;
	}

//	getAllByCustIdAndDateAndType
	public List<Consignment> getAllByCustIdAndDateAndType(String custId, String applyStart, String applyEnd,
			String type) throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		pType = ptRepository.findById(Integer.parseInt(type)).orElse(null);
		List<Consignment> consignments = repository.findAllByCustomerAndApplyDateBetweenAndProductType(cust,
				formatter(applyStart), formatter(applyEnd), pType);
		return consignments;
	}

//	getAllByCustIdDateAndReview
	public List<Consignment> getAllByCustIdDateAndReview(String custId, String applyStart, String applyEnd,
			String review) throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		List<Consignment> consignments = repository.findAllByCustomerAndApplyDateBetweenAndReview(cust,
				formatter(applyStart), formatter(applyEnd), Integer.parseInt(review));
		return consignments;
	}

//	getAllByCustIdAndDate
	public List<Consignment> getAllByCustIdAndDate(String custId, String applyStart, String applyEnd) throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		List<Consignment> consignments = repository.findAllByCustomerAndApplyDateBetween(cust, formatter(applyStart),
				formatter(applyEnd));
		return consignments;
	}

//	getAllByCustIdAndApplyStartAndTypeAndReview
	public List<Consignment> getAllByCustIdAndApplyStartAndTypeAndReview(String custId, String applyStart, String type,
			String review) throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		pType = ptRepository.findById(Integer.parseInt(type)).orElse(null);
		List<Consignment> consignments = repository
				.findAllByCustomerAndApplyDateGreaterThanEqualAndProductTypeAndReview(cust, formatter(applyStart),
						pType, Integer.parseInt(review));
		return consignments;
	}

//	getAllByCustIdAndApplyStartAndType
	public List<Consignment> getAllByCustIdAndApplyStartAndType(String custId, String applyStart, String type)
			throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		pType = ptRepository.findById(Integer.parseInt(type)).orElse(null);
		List<Consignment> consignments = repository.findAllByCustomerAndApplyDateGreaterThanEqualAndProductType(cust,
				formatter(applyStart), pType);
		return consignments;
	}

//	getAllByCustIdAndApplyStartAndReview
	public List<Consignment> getAllByCustIdAndApplyStartAndReview(String custId, String applyStart, String review)
			throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		List<Consignment> consignments = repository.findAllByCustomerAndApplyDateGreaterThanEqualAndReview(cust,
				formatter(applyStart), Integer.parseInt(review));
		return consignments;
	}

//	getAllByCustIdAndApplyStart
	public List<Consignment> getAllByCustIdAndApplyStart(String custId, String applyStart) throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		List<Consignment> consignments = repository.findAllByCustomerAndApplyDateGreaterThanEqual(cust,
				formatter(applyStart));
		return consignments;
	}

//	getAllByCustIdAndApplyEndAndTypeAndReview
	public List<Consignment> getAllByCustIdAndApplyEndAndTypeAndReview(String custId, String applyEnd, String type,
			String review) throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		pType = ptRepository.findById(Integer.parseInt(type)).orElse(null);
		List<Consignment> consignments = repository.findAllByCustomerAndApplyDateLessThanEqualAndProductTypeAndReview(
				cust, formatter(applyEnd), pType, Integer.parseInt(review));
		return consignments;
	}

//	getAllByCustIdAndApplyEndAndType
	public List<Consignment> getAllByCustIdAndApplyEndAndType(String custId, String applyEnd, String type)
			throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		pType = ptRepository.findById(Integer.parseInt(type)).orElse(null);
		List<Consignment> consignments = repository.findAllByCustomerAndApplyDateLessThanEqualAndProductType(cust,
				formatter(applyEnd), pType);
		return consignments;
	}

//	getAllByCustIdAndApplyEndAndReview
	public List<Consignment> getAllByCustIdAndApplyEndAndReview(String custId, String applyEnd, String review)
			throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		List<Consignment> consignments = repository.findAllByCustomerAndApplyDateLessThanEqualAndReview(cust,
				formatter(applyEnd), Integer.parseInt(review));
		return consignments;
	}

//	getAllByCustIdAndApplyEnd
	public List<Consignment> getAllByCustIdAndApplyEnd(String custId, String applyEnd) throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		List<Consignment> consignments = repository.findAllByCustomerAndApplyDateLessThanEqual(cust,
				formatter(applyEnd));
		return consignments;
	}

//	getAllByCustIdAndTypeAndReview
	public List<Consignment> getAllByCustIdAndTypeAndReview(String custId, String type, String review)
			throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		List<Consignment> consignments = repository.findAllByCustomerAndProductTypeAndReview(cust, pType,
				Integer.parseInt(review));
		return consignments;
	}

//	getAllByCustIdAndType
	public List<Consignment> getAllByCustIdAndType(String custId, String type) throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		pType = ptRepository.findById(Integer.parseInt(type)).orElse(null);
		List<Consignment> consignments = repository.findAllByCustomerAndProductType(cust, pType);
		return consignments;
	}

//	getAllCustIdAndByReview
	public List<Consignment> getAllByCustIdAndReview(String custId, String review) throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		List<Consignment> consignments = repository.findAllByCustomerAndReview(cust, Integer.parseInt(review));
		return consignments;
	}

//	deleteConsignmentById
	public Response deleteConsignmentById(String id) {
		Response response = new Response();
		try {
			repository.deleteById(Integer.parseInt(id));
			response.setSuccess(true);
		} catch (Exception e) {
			System.out.println(e);
			response.setSuccess(false);
		}

		return response;
	}

//	createConsignment
	public Response createConsigment(ConsignDTO consign, MultipartFile[] files, String custId) {
		Response response = new Response();
		cust = cRepository.findById(Integer.parseInt(custId)).orElse(null);
		pType = ptRepository.findById(Integer.parseInt(consign.getpType())).orElse(null);

		System.out.println(consign.getProductName());
		if (cust != null && pType != null) {
			try {
				response.setSuccess(true);
				Consignment newConsign = new Consignment();
				newConsign.setCustomer(cust);
				newConsign.setProductType(pType);
				newConsign.setProductName(consign.getProductName());
				newConsign.setQuantity(consign.getQuantity());
				newConsign.setProduceYear(consign.getProduceYear());
				newConsign.setpCondition(consign.getpCondition());
				newConsign.setDelivery(consign.getDelivery());
				newConsign.setDeliveryDate(consign.getDeliveryDate());
				newConsign.setApplyDate(new Date());
				newConsign.setReview(0);

				newConsign.setPic1(files[0].getBytes());
				newConsign.setPic2(files[1].getBytes());
				newConsign.setPic3(files[2].getBytes());

				repository.save(newConsign);

				response.setSuccess(true);

			} catch (Exception e) {
				response.setSuccess(false);
				response.setMesg("託售申請儲存失敗");
			}
		} else {
			response.setSuccess(false);
			response.setMesg((cust == null ? "查無此顧客" : "") + (pType == null ? " 查無此類別" : ""));
		}
		return response;
	}

//	============= 後台API ===============

//	getAllByTypeAndReviewAndDelivery
	public List<Consignment> getAllByProductTypeAndReviewAndDelivery(String type, String review, String delivery) {
		pType = ptRepository.findById(Integer.parseInt(type)).orElse(null);
		List<Consignment> consigments = null;

		if (pType != null && review != null && delivery != null) {
			if (Integer.parseInt(delivery) > 0) {
				consigments = repository.findAllByProductTypeAndReviewAndDeliveryAndReceivedIsFalse(pType,
						Integer.parseInt(review), Integer.parseInt(delivery));
			} else {
				consigments = repository.findAllByProductTypeAndReviewAndReceivedIsTrue(pType,
						Integer.parseInt(review));
			}
		}

		return consigments;
	}

//	getAllByTypeAndReview
	public List<Consignment> getAllByProductTypeAndReview(String type, String review) {
		pType = ptRepository.findById(Integer.parseInt(type)).orElse(null);
		List<Consignment> consigments = null;

		if (pType != null && review != null) {
			consigments = repository.findAllByProductTypeAndReview(pType, Integer.parseInt(review));
		}

		return consigments;
	}

//	getAllByTypeAndDelivery
	public List<Consignment> getAllByProductTypeAndDelivery(String type, String delivery) {
		pType = ptRepository.findById(Integer.parseInt(type)).orElse(null);
		List<Consignment> consigments = null;

		if (pType != null && delivery != null) {
			if (Integer.parseInt(delivery) > 0) {
				consigments = repository.findAllByProductTypeAndDeliveryAndReceivedIsFalse(pType, Integer.parseInt(delivery));
			} else {
				consigments = repository.findAllByProductTypeAndReceivedIsTrue(pType);
			}
		}

		return consigments;
	}

//	getAllByProductType
	public List<Consignment> getAllByProductType(String type) {
		pType = ptRepository.findById(Integer.parseInt(type)).orElse(null);
		List<Consignment> consigments = null;

		if (pType != null) {
			consigments = repository.findAllByProductType(pType);
		}

		return consigments;
	}

//	getAllByReviewAndDelivery
	public List<Consignment> getAllByReviewAndDelivery(String review, String delivery) {
		List<Consignment> consigments = null;

		if (review != null && delivery != null) {
			if (Integer.parseInt(delivery) > 0) {
				consigments = repository.findAllByReviewAndDeliveryAndReceivedIsFalse(Integer.parseInt(review),
						Integer.parseInt(delivery));
			} else {
				consigments = repository.findAllByReviewAndReceivedIsTrue(Integer.parseInt(review));
			}
		}

		return consigments;
	}

//	getAllByReview
	public List<Consignment> getAllByReview(String review) {
		List<Consignment> consigments = null;

		if (review != null) {
			consigments = repository.findAllByReview(Integer.parseInt(review));
		}

		return consigments;
	}

//	getAllByDelivery
	public List<Consignment> getAllByDelivery(String delivery) {
		List<Consignment> consigments = null;

		if (delivery != null) {
			if (Integer.parseInt(delivery) > 0) {
				consigments = repository.findAllByDeliveryAndReceivedIsFalse(Integer.parseInt(delivery));
			} else {
				consigments = repository.findAllByReceivedIsTrue();
			}
		}

		return consigments;
	}

//	editConsigment
	public Response editConsignment(String id, String review, String price) {
		Response response = new Response();
		Consignment consign = repository.findById(Integer.parseInt(id)).orElse(null);

		if (consign != null) {
			Integer originReview = consign.getReview();
			consign.setReview(Integer.parseInt(review));
			if (price != "") {
				consign.setPrice(Integer.parseInt(price));
			} else {
				consign.setPrice(null);
			}
			repository.save(consign);

//			紅利點數機制 => 成功託售商品，一件商品5點，每五件加10點
			Customer cust = consign.getCustomer();
			List<Consignment> consigns;

			if (originReview != 1) {
				if (Integer.parseInt(review) == 1) {
					System.out.println("point1");
					cust.setPoints(cust.getPoints() + 5);
					cRepository.save(cust);

					consigns = repository.findAllByCustomerAndPointDateIsNullAndReview(cust, 1);
					System.out.println("consigns.size():" + consigns.size());
					if (consigns.size() % 5 == 0) {
						System.out.println("point2");

						for (Consignment item : consigns) {
							item.setPointDate(new Date());
							repository.save(item);
						}

						cust.setPoints(cust.getPoints() + 10);
						cRepository.save(cust);
					}

				}
			} else {
				if (Integer.parseInt(review) != 1) {
					System.out.println("point3");

					cust.setPoints(cust.getPoints() - 5);
					consigns = repository.findAllByNearestPointDate();
					if (consigns.size() % 5 == 0) {
						System.out.println("point4");

						for (Consignment item : consigns) {
							item.setPointDate(null);
							repository.save(item);
						}
						cust.setPoints(cust.getPoints() - 10);
						cRepository.save(cust);
					}
				}
			}

			response.setSuccess(true);
			response.setMesg("修改成功");

		} else {
			response.setSuccess(false);
			response.setMesg("查無紀錄");

		}

		return response;
	}

	public Response receiveConsignment(String id, String received) {
		Response response = new Response();
		Consignment target = repository.findById(Integer.parseInt(id)).orElse(null);
		if (target != null) {
			target.setReceived(Boolean.valueOf(received));
			repository.save(target);
			response.setSuccess(true);
		} else {
			response.setSuccess(false);
			response.setMesg("查無此紀錄");
		}
		return response;
	}

	private Date formatter(String dateStr) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.parse(dateStr);
	}
}
