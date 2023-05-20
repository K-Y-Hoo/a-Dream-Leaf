//
//  AccountRepository.swift
//  aDreamLeaf
//
//  Created by 엄태양 on 2023/05/18.
//

import Foundation
import RxSwift
import FirebaseAuth

struct AccountRepository {
    private let network = AccountNetwork()
    
    func setBudget(to budget: Int) -> Observable<RequestResult> {
        return network.setAccountBudget(to: budget)
    }
    
    func createRequest(date: String, storeName: String, body: String, price: Int) -> Observable<RequestResult> {
        
        if let inputValidationResult = inputValidation(date: date, storeName: storeName, body: body, price: price) {
            return inputValidationResult
        }
        
        return network.createAccountServer(date: date, storeName: storeName, body: body, price: price)
    }
    
    func getExpenditureList(when: String) -> Observable<ExpenditureListResult> {
        return network.getExpenditureList(when: when)
    }
    
    private func inputValidation(date: String, storeName: String, body: String, price: Int) -> Observable<RequestResult>? {
        if storeName == "" {
            return Observable.just(RequestResult(success: false, msg: "가게명을 입력해주세요."))
        } else if price < 0 {
            return Observable.just(RequestResult(success: false, msg: "가격을 입력해주세요."))
        } else {
            return nil
        }
    }
}
