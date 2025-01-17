//
//  ReviewNetwork.swift
//  aDreamLeaf
//
//  Created by 엄태양 on 2023/05/22.
//

import Foundation
import RxSwift
import Alamofire
import FirebaseAuth

struct ReviewNetwork {
    func createRequest(storeId: Int, body: String, rating: Int) -> Observable<RequestResult<Void>> {
        return Observable.create { observer in
            
            Auth.auth().currentUser?.getIDToken() { token, error in
                
                if error != nil {
                    print(error)
                    observer.onNext(RequestResult(success: false, msg: "오류가 발생했습니다.\n잠시후에 다시 시도해주세요."))
                }
                
                guard let token = token else { return }
                
                let url = K.serverURL + "/review/create"
                var request = URLRequest(url: URL(string: url)!)
                request.httpMethod = "POST"
                request.setValue("application/json", forHTTPHeaderField: "Content-Type")
                request.timeoutInterval = 10
                // POST 로 보낼 정보
                let params = CreateReviewRequest(firebaseToken: token, storeId: storeId, date: Date.now, body: body, rating: rating).toDict()
                 
                 // httpBody 에 parameters 추가
                do {
                    try request.httpBody = JSONSerialization.data(withJSONObject: params, options: [])
                } catch {
                    print("http Body Error")
                    observer.onNext(RequestResult(success: false, msg: "오류가 발생했습니다! \n잠시 후에 다시 시도해주세요!"))
                }
                
                AF.request(request).responseJSON{ (response) in
                     switch response.result {
                         case .success:
                             do {
                                 observer.onNext(RequestResult(success: true, msg: nil))
                             } catch(let err) {
                                 print(err)
                                 observer.onNext(RequestResult(success: false, msg: "오류가 발생했습니다! \n 잠시 후에 다시 시도해주세요!"))
                             }
                                 
                         case .failure(let error):
                                 print("error : \(error.errorDescription!)")
                                 observer.onNext(RequestResult(success: false, msg: "오류가 발생했습니다! \n 잠시 후에 다시 시도해주세요!"))
                     }
                 }
                
            }
            
            return Disposables.create()
        }
    }
    
    func fetchRecentRequest(storeId: Int) -> Observable<RequestResult<[Review]>> {
        return Observable.create { observer in
            
            Auth.auth().currentUser?.getIDToken() { token, error in
                
                if error != nil {
                    print(error)
                    observer.onNext(RequestResult<[Review]>(success: false, msg: "오류가 발생했습니다.\n잠시후에 다시 시도해주세요.", data: nil))
                }
                
                guard let token = token else { return }
                
                let url = K.serverURL + "/review/\(storeId)?page=1&display=3".addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!
                var request = URLRequest(url: URL(string: url)!)
                
                request.httpMethod = "GET"
                request.setValue("application/json", forHTTPHeaderField: "Content-Type")
                request.timeoutInterval = 10
            
                
                AF.request(request).responseJSON{ (response) in
                     switch response.result {
                         case .success:
                             do {
                                 print(response.response?.statusCode)
                                 
                                 guard let result = response.data else {return}
                                 
                                 let decoder = JSONDecoder()
                                 let data = try decoder.decode([Review].self, from: result)
                                 
                                 observer.onNext(RequestResult<[Review]>(success: true, msg: nil, data: data))
                             } catch(let err) {
                                 print(err)
                                 observer.onNext(RequestResult<[Review]>(success: false, msg: "오류가 발생했습니다! \n 잠시 후에 다시 시도해주세요!", data: nil))
                             }
                                 
                         case .failure(let error):
                                 print("error : \(error.errorDescription!)")
                                 observer.onNext(RequestResult<[Review]>(success: false, msg: "오류가 발생했습니다! \n 잠시 후에 다시 시도해주세요!", data: nil))
                     }
                 }
                
            }
            
            return Disposables.create()
        }
    }
}
