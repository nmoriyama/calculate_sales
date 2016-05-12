package jp.co.plusize.moriyama_naoki.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CalculateSales {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException{
		ArrayList<String> rcdFileList = new ArrayList<String>();
		ArrayList<Integer> sequenceCheck = new ArrayList<Integer>();

		Map<String, Long> branchSales = new HashMap<String, Long>();//キー:支店コード , 要素:売上金額
		Map<String, String> branchName = new HashMap<String, String>();
		Map<String, Long> commoditySales = new HashMap<String, Long>();//キー:商品コード , 要素:売上金額
		Map<String, String> commodityName = new HashMap<String, String>();

		String[] branch = {"branch.lst", "branch.out", "支店", "^[0-9]{3}$"};
		String[] commodity = {"commodity.lst", "commodity.out", "商品", "^[0-9a-zA-Z]{8}$"};
		int[] branchNumber = {0, 3};
		int[] commodityNumber = {1, 8};
		BufferedReader bufferedReader = null;
		
		//コマンドライン引数1つじゃない場合
		if (args.length != 1) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		//支店定義ファイルの読み込み
		branchName = fileScan(args[0], branch);		 
		if (branchName.containsKey("error")) {
			System.out.println(branchName.get("error"));
			return;
		}
		for (String getCode : branchName.keySet()) {
			branchSales.put(getCode, 0L);
		}
		//商品定義ファイルの読み込み
		commodityName = fileScan(args[0], commodity);		
		if (commodityName.containsKey("error")) {
			System.out.println(commodityName.get("error"));
			return;
		}
		for (String getCode : commodityName.keySet()) {
			commoditySales.put(getCode,0L);
		}
		//売上ファイルのある場所の検索
		try {
			File file = new File(args[0]);
			String[] allFile = file.list();//探す場所にあるファイル全部
			File[] checkDirectory = file.listFiles();
			for (int i = 0; i < allFile.length; i ++) {
				if (! checkDirectory[i].isDirectory() && allFile[i].matches("^[0-9]{8}.rcd$")) {	//8桁の数字.rcdか
					rcdFileList.add(allFile[i]);
				}
			}
			
			//ファイル読み込み
			for (int i = 0; i < rcdFileList.size(); i ++) {
				File surchFile = new File(args[0] + File.separator + rcdFileList.get(i));
				sequenceCheck.add(i);//読み込めたらsizeが一つ増える
				//連番チェック
				if (sequenceCheck.size() != Integer.parseInt(rcdFileList.get(i).substring(0, 8))) {
					System.out.println("売上ファイル名が連番になっていません");//読み込めなかったら
					return;
				}
				FileReader fileReader = new FileReader(surchFile);
				bufferedReader = new BufferedReader(fileReader);
				String line;
				ArrayList<String> rcdLoaded = new ArrayList<String>();
				while ((line = bufferedReader.readLine()) != null) {
					rcdLoaded.add(line);
				}
				if (rcdLoaded.size() != 3) {//３行じゃない場合
					System.out.println(rcdFileList.get(i) + "のフォーマットが不正です");
					return;
				}
				if (! branchName.containsKey(rcdLoaded.get(branchNumber[0]))) {
					System.out.println(rcdFileList.get(i) + "の" + branch[2] + "コードが不正です");
					return;
				}
				branchSales = aggregate(branchNumber[0], rcdLoaded, branchSales); //支店ごとの売り上げ
				if (branchSales.containsKey("error")) {
					return;
				}
				if (! commodityName.containsKey(rcdLoaded.get(commodityNumber[0]))) {
					System.out.println(rcdFileList.get(i) + "の" + commodity[2] + "コードが不正です");
					return;
				}
				commoditySales = aggregate(commodityNumber[0], rcdLoaded, commoditySales); //商品ごとの売り上げ
				if (commoditySales.containsKey("error")) {
					return;
				}
			}
			//4集計結果出力
			filePrint(args[0], branch[1], branchSales, branchName);
			filePrint(args[0], commodity[1], commoditySales, commodityName);
		} catch (IOException  e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} finally {
			bufferedReader.close();
		}
	}


	//支店定義ファイル,商品定義ファイルの読み込み
	public static Map<String, String> fileScan(String place, String[] lstFile) throws IOException {
		Map<String, String> fileScan = new HashMap<String, String>();
		File file = new File(place + File.separator + lstFile[0]);
		if (! file.exists()) {
			fileScan.put("error", lstFile[2] + "定義ファイルが存在しません");
			return fileScan;
		}
		FileReader fileReader = new FileReader(file);
		@SuppressWarnings("resource")
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			String[] lineDivision = line.split(",");// , で分割
			if (lineDivision.length != 2 || ! lineDivision[0].matches(lstFile[3])) {//読み込んだファイルの形式が正しいか
				fileScan.put("error", lstFile[2] + "定義ファイルのフォーマットが不正です");
				return fileScan;
			}
			fileScan.put(lineDivision[0], lineDivision[1]);//キー:コード , 要素:名前	
		}
		bufferedReader.close();
		return fileScan;
	}
	//各合計売上の計算
	public static Map<String, Long> aggregate(int code, ArrayList<String> rcdLoaded, Map<String, Long> categorySales) throws IOException {
		Long rcdSales = new Long(rcdLoaded.get(2));
		Long aggregate = categorySales.get(rcdLoaded.get(code)) + rcdSales;
		if (aggregate >= 10000000000L) {//10桁を超えた場合
			categorySales.put("error", 10000000001L);
			System.out.println("合計金額が10桁を超えました");
			return categorySales;
		}
		categorySales.put(rcdLoaded.get(code), aggregate);
		return categorySales;
	}
	//支店ごと,商品ごとの売り上げ
	public static void filePrint(String place, String fileName, Map<String, Long> sales, Map<String, String> namePosition) throws IOException {
		String crlf = System.getProperty("line.separator");
		//並び替え
		List<Entry<String, Long>> sort = new ArrayList<Entry<String, Long>>(sales.entrySet());
		Collections.sort(sort, new Comparator<Entry<String, Long>>() {
			public int compare(Entry<String, Long> sort1, Entry<String, Long> sort2) {
				return (sort2.getValue()).compareTo(sort1.getValue());
			}
		} );
		//ここまで
		File file = new File(place + File.separator + fileName);
		FileWriter fileWriter = new FileWriter(file);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		for (Entry<String,Long> i : sort) {
			bufferedWriter.write(i.getKey() + "," + namePosition.get(i.getKey()) + "," + i.getValue() + crlf);
		}
		bufferedWriter.close();
		return;
	}
}