//
//  ChartView.swift
//  aDreamLeaf
//
//  Created by 엄태양 on 2023/04/05.
//

import UIKit
import Charts

class UIChartViewController: UIViewController {
    let accountSummaryContainer = UIView()
    private let accountTitleLabel = UILabel()
    
    private let pieChart = PieChartView()
    private let dataValues = [50000, 12000]
    
    private let usedAmountColorView = UIView()
    private let usedAmountLabel = UILabel()
    private let accountMoreButton = UIButton()
    
    private let balanceColorView = UIView()
    private let balanceLabel = UILabel()
    
    init() {
        super.init(nibName: nil, bundle: nil)
        
        chartLayout()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func chartSetting() {
        accountSummaryContainer.backgroundColor = UIColor(white: 0.95, alpha: 1)
        accountSummaryContainer.layer.cornerRadius = 10
        
        accountTitleLabel.text = "가계부 요약"
        accountTitleLabel.font = .systemFont(ofSize: 20, weight: .semibold)
        accountTitleLabel.textColor = .black
        
        accountMoreButton.setTitle("더보기", for: .normal)
        accountMoreButton.setTitleColor(.gray, for: .normal)
        accountMoreButton.titleLabel?.font = .systemFont(ofSize: 13, weight: .semibold)
        
        pieChart.drawEntryLabelsEnabled = false
        pieChart.legend.enabled = false
        self.setPieChartData(pieChart: self.pieChart, with: self.entryData(values: dataValues))
        
        usedAmountColorView.backgroundColor = UIColor(white: 0.85, alpha: 1)
        usedAmountLabel.text = "사용액: \(NumberUtil.commaString(dataValues[0])!)원"
        usedAmountLabel.textColor = .black
        usedAmountLabel.font = .systemFont(ofSize: 15, weight: .regular)
        
        balanceColorView.backgroundColor = UIColor(named: "subColor")!
        balanceLabel.text = "잔액: \(NumberUtil.commaString(dataValues[1])!)원"
        balanceLabel.textColor = .black
        balanceLabel.font = .systemFont(ofSize: 15, weight: .regular)
    }
    
    private func setPieChartData(pieChart: PieChartView, with: [ChartDataEntry]) {
        
        let dataSet = PieChartDataSet(entries: with, label: "가계부 요약")
        dataSet.colors = colorsOfCharts()
        dataSet.drawValuesEnabled = false
        dataSet.label = ""
        
        let data = PieChartData(dataSet: dataSet)
        
        pieChart.data = data
        
    }
    
    private func entryData(values: [Int]) -> [PieChartDataEntry] {
        var dataEntries = [PieChartDataEntry]()
        
        for i in 0..<values.count {
            let dataEntry = PieChartDataEntry(value: Double(dataValues[i]))
            dataEntries.append(dataEntry)
        }
        
        return dataEntries
    }
    
    private func colorsOfCharts() -> [UIColor] {
        return [UIColor(white: 0.85, alpha: 1), UIColor(named: "subColor")!]
    }
    
    private func chartLayout() {
        
        [accountTitleLabel, accountMoreButton, pieChart, usedAmountColorView, usedAmountLabel, balanceColorView, balanceLabel].forEach {
            accountSummaryContainer.addSubview($0)
            $0.translatesAutoresizingMaskIntoConstraints = false
        }
        
        [
            accountSummaryContainer.heightAnchor.constraint(equalToConstant: 200),
            
            accountTitleLabel.topAnchor.constraint(equalTo: accountSummaryContainer.topAnchor, constant: 10),
            accountTitleLabel.leadingAnchor.constraint(equalTo: accountSummaryContainer.leadingAnchor, constant: 15),
            
            accountMoreButton.trailingAnchor.constraint(equalTo: accountSummaryContainer.trailingAnchor, constant: -10),
            accountMoreButton.centerYAnchor.constraint(equalTo: accountTitleLabel.centerYAnchor),
            accountMoreButton.widthAnchor.constraint(equalToConstant: 60),
            
            pieChart.topAnchor.constraint(equalTo: accountTitleLabel.bottomAnchor, constant: 10),
            pieChart.leadingAnchor.constraint(equalTo: accountTitleLabel.leadingAnchor),
            pieChart.trailingAnchor.constraint(equalTo: accountSummaryContainer.centerXAnchor, constant: -10),
            pieChart.bottomAnchor.constraint(equalTo: accountSummaryContainer.bottomAnchor, constant: -10),
            
            usedAmountColorView.heightAnchor.constraint(equalToConstant: 15),
            usedAmountColorView.widthAnchor.constraint(equalToConstant: 15),
            usedAmountColorView.leadingAnchor.constraint(equalTo: accountSummaryContainer.centerXAnchor, constant: 10),
            usedAmountColorView.bottomAnchor.constraint(equalTo: pieChart.centerYAnchor, constant: -10),
            
            usedAmountLabel.leadingAnchor.constraint(equalTo: usedAmountColorView.trailingAnchor, constant: 10),
            usedAmountLabel.centerYAnchor.constraint(equalTo: usedAmountColorView.centerYAnchor),
            
            balanceColorView.heightAnchor.constraint(equalToConstant: 15),
            balanceColorView.widthAnchor.constraint(equalToConstant: 15),
            balanceColorView.leadingAnchor.constraint(equalTo: accountSummaryContainer.centerXAnchor, constant: 10),
            balanceColorView.bottomAnchor.constraint(equalTo: pieChart.centerYAnchor, constant: 10),
            
            balanceLabel.leadingAnchor.constraint(equalTo: balanceColorView.trailingAnchor, constant: 10),
            balanceLabel.centerYAnchor.constraint(equalTo: balanceColorView.centerYAnchor),
        ].forEach { $0.isActive = true }
    }
    
    func hideMoreButton() {
        accountMoreButton.isHidden = true
    }
}