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
import java.util.TreeMap;

public class calculate_sales {
	public static void main(String[] args){
		ArrayList<String> dataList = new ArrayList<String>();
		ArrayList<String> rcdFileList = new ArrayList<String>();
		
		Map<String,Integer> branSales = new HashMap<String,Integer>();
		HashMap<String,String> branName = new HashMap<String,String>();
		HashMap<Integer,String> branCode = new HashMap<Integer,String>();
		HashMap<String,Integer> branCodeSalesMap = new HashMap<String,Integer>();

		
		Map<String,Integer> comSales = new HashMap<String,Integer>();
		HashMap<String,String> comName = new HashMap<String,String>();
		HashMap<Integer,String> comCode = new HashMap<Integer,String>();
		HashMap<String,Integer> comCodeSales = new HashMap<String,Integer>();
		
		
		//TreeMap<Integer,String> BranchMap = new TreeMap<Integer,String>();
		//Map<String, Integer> BranSort = new HashMap<String, Integer>();
		TreeMap<Integer,String> CommodityMap = new TreeMap<Integer,String>();

		int rcdSize = 0;
		
		//1支店定義ファイルの読み込み
		try{
			//コマンドライン引数がない場合
			
			if(args.length == 0){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
			if(args.length > 1){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
			
			if(args[0].endsWith(File.pathSeparator)){
				args[0] = args[0].substring(0,args[0].length()-1);
			}
			File branchFile = new File(args[0] + File.separator +"branch.lst");
			
			if(!branchFile.exists()){
				System.out.println("支店定義ファイルが存在しません");
				return;
			}
			
			FileReader fileReader = new FileReader(branchFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			try{
				String line;
				int count = 0;
				while((line = bufferedReader.readLine()) != null){
					String[] branch = line.split(",");// , で分割
					branSales.put(branch[0],0);//キー:支店コード , 要素:売上金額
					branName.put(branch[0],branch[1]);//キー:支店コード , 要素:支店名
					branCode.put(count,branch[0]);//キー:０～ , 要素：支店コード
					//１行 , が２つ以上多くある もしくは 支店コードが３桁でない 場合
					if(branch.length != 2 ){ 
						System.out.println("支店定義ファイルのフォーマットが不正です");
						return;
					}
					if (!branch[0].matches("^[0-9]{3}$") || branch[0].length() != 3) {//数字のみか
						System.out.println("支店定義ファイルのフォーマットが不正です");
						return;
					}
					count++;
				}
			}catch(NumberFormatException e){
				System.out.println("支店定義ファイルのフォーマットが不正です");
				return;
			}catch(ArrayIndexOutOfBoundsException e){
				System.out.println("支店定義ファイルのフォーマットが不正です");
				return;
			}
			finally {
				bufferedReader.close();
			}
		}catch(IOException  e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		
		
		//2商品定義ファイルの読み込み
		try{ 
			File comFile = new File(args[0] + File.separator + "commodity.lst");
			
			if(!comFile.exists()){
				System.out.println("商品定義ファイルが存在しません");
				return;
			}
			
			FileReader comFR = new FileReader(comFile);
			BufferedReader comBR = new BufferedReader(comFR);
			
			try{
				String line;
				int count = 0;
				while((line = comBR.readLine()) != null){
					String[] commodity = line.split(",");// , で分割
					comSales.put(commodity[0],0);//キー:商品コード , 要素:売上金額
					comName.put(commodity[0],commodity[1]);//キー:商品コード , 要素:商品名
					comCode.put(count, commodity[0]);//キー:０～ , 要素：商品コード
					//１行 , が２つ以上多くある もしくは 商品コードが８桁でない 場合
					if(commodity.length != 2){
						System.out.println("商品定義ファイルのフォーマットが不正です");
						return;
					}
					if (!commodity[0].matches("^[0-9a-zA-Z]{8}$") || commodity[0].length() != 8) {//アルファベット、数字のみか
						System.out.println("商品定義ファイルのフォーマットが不正です");
						return;
					}
					count++;
				}
			}catch(NumberFormatException e){
				System.out.println("商品定義ファイルのフォーマットが不正です");
				return;
			}catch(ArrayIndexOutOfBoundsException e){
				System.out.println("商品定義ファイルのフォーマットが不正です");
				return;
			}
			finally{
				comBR.close();
			}
		}catch(IOException  e){
			System.out.println("予期せぬエラーが発生しました");
			System.out.println(e);
			return;
		}

		
		//3集計
		//売上ファイルのある場所の検索
		File rcdFile = new File(args[0]);
		
		String[] rcdFileString = rcdFile.list();
		for(int i = 0;i <rcdFileString.length;i ++){
			String line = rcdFileString[i];
			
			File[] filelist = rcdFile.listFiles();

			if (filelist[i].isFile()){
				// ファイルだった時の処理
			}else if (filelist[i].isDirectory()){
				String surchNum = String.valueOf(i+2);
				String surch = "0";
				while(surch.length() < (8 - surchNum.length())){
					surch += "0";
				}
				surch = surch.concat(surchNum);
				File surchFile = new File(args[0] + File.separator + surch + ".rcd");
				if(!surchFile.exists()){
					//見つからなかったら
					rcdSize = 1;
					i++;
				}else{
					//見つかったら
					System.out.println("売上ファイル名が連番になっていません");
					return;
				}
			}
			//拡張子がrcd かつ 12桁(８桁+拡張子(.rcd))
			if(line.endsWith("rcd") && line.length() == 12){
				Integer.parseInt(line.substring(0,8));//rcdファイルが数字かどうか
				rcdFileList.add(line);
			}else if(line.endsWith("lst") || line.endsWith("out") || line.endsWith("java")){

			}else{				
				System.out.println("売上ファイル名が連番になっていません");
				return;
			}
		}//rcdファイル探し終わり

		
		//ファイル読み込み
		try{
			int rcdNumber = 1;
			for(int i = 0;i < rcdFileList.size() - rcdSize;i ++){
				String RcdNumber = String.valueOf(rcdNumber);
				String code = "0";
				while(code.length() < (8 - RcdNumber.length())){
					code += "0";
				}
				code = code.concat(RcdNumber);//comStringにファイル名
				File rcdSalesFile = new File(args[0] + File.separator + code + ".rcd");
				
				//連番チェック
				if(!rcdSalesFile.exists()){//読み込めなかったら
					RcdNumber = String.valueOf(rcdNumber + 1);
					code = "0";
					while(code.length() < (8 - RcdNumber.length())){
						code += "0";
					}
					code = code.concat(RcdNumber);
					rcdSalesFile = new File(args[0] + File.separator + code + ".rcd");
					
					
					if(!rcdSalesFile.exists()){//読み込めなかった場合
						System.out.println("予期せぬエラーが発生しました");
						return;
					}else{//連番じゃなかった場合
						System.out.println("売上ファイル名が連番になっていません");
						return;
					}
				}	
				
				FileReader rcdSalesFileFR = new FileReader(rcdSalesFile);
				BufferedReader rcdSalesFileBR = new BufferedReader(rcdSalesFileFR);
				
				try{
					long branchSales = 0,comoditySales = 0;
					String line;
					
					int k = 0;
					String surch[] = new String[2];
					while((line = rcdSalesFileBR.readLine()) != null){
						dataList.add(line);
						/*if(k == 0){ //１行目に支店コードがない場合
							surch[0] = line;
						}else if(k == 1){//２行目に商品コードがない場合
							surch[1] = line;
						}*/
						if(k < 2){
							surch[k] = line;
						}
						k++;
					}

					if(k != 3){
						System.out.println(code + ".rcdのフォーマットが不正です");	
						return;
					}
					
					if(surch[0].length() != 3) { //１行目に支店コードがない場合
						System.out.println(code + ".rcdの支店コードが不正です");
						return;
					}else if(surch[1].length() != 8){//２行目に商品コードがない場合	
						System.out.println(code + ".rcdの商品コードが不正です");	
						return;
					}
					long rcdSales = new Long(dataList.get(2));//今回取得した売上
					branchSales = branSales.get(dataList.get(0)) + rcdSales;//支店売上
					comoditySales = comSales.get(dataList.get(1)) + rcdSales;//商品売上
					
					if(branchSales >= 1000000000 || comoditySales >= 1000000000){
						System.out.println("合計金額が10桁を超えました");
						return;
					}
					
					branSales.put(dataList.get(0),(int)branchSales);
					branCodeSalesMap.put(dataList.get(0),(int)branchSales);//支店コードと売上
 
					comSales.put(dataList.get(1),(int)comoditySales);
					comCodeSales.put(dataList.get(1),(int)comoditySales);//商品コードと売上
					rcdNumber ++;
					dataList.clear();
					
				}catch(IOException e){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
				finally{
					rcdSalesFileBR.close();				
				}
			}
			
		}catch(IOException  e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		
		//4集計結果出力		
		try{
			//並び替え
			List<Map.Entry<String,Integer>> entries = new ArrayList<Map.Entry<String,Integer>>(branSales.entrySet());
	        Collections.sort(entries, new Comparator<Map.Entry<String,Integer>>() {
	        public int compare(
	            Entry<String,Integer> entry1, Entry<String,Integer> entry2) {
	        	return ((Integer)entry2.getValue()).compareTo((Integer)entry1.getValue());
	        }
	        });
			//ここまで
	        File branOutFile = new File(args[0] + File.separator + "branch.out");
			branOutFile.createNewFile();
			FileWriter branOutFileFW = new FileWriter(branOutFile);
			BufferedWriter branOutFileBW = new BufferedWriter(branOutFileFW);	
			try{
				//int count = 0;
				//int SortList[];
				for (Entry<String,Integer> s : entries) {
		            branOutFileBW.write(s.getKey() + "," + branName.get(s.getKey()) + "," + s.getValue() + "\r\n");
		        }
				/*for(int i = 0;i < branSales.size();i++){
				
					if(branCodeSalesMap.get(branCode.get(count)) != null){
						//BranchMap.put(branCodeSalesMap.get(branCode.get(count)),branCode.get(count));//treemap にキー:売上金額 要素:支店コード
					}
					count++;
				}
			//書き込み
				branOutFileBW.write(BranchMap.get(BranchMap.lastKey()) + "," + branName.get(BranchMap.get(BranchMap.lastKey())) + "," + BranchMap.lastKey() + "\r\n");
				int branKey = BranchMap.lastKey();
				while(BranchMap.lowerKey(branKey) != null){
					if(branX.containsKey(BranchMap.lowerKey(branKey))){
						for(int i=0;i < branX.get(BranchMap.lowerKey(branKey));i++){
							
						}
						
					}else{
						branOutFileBW.write(BranchMap.get(BranchMap.lowerKey(branKey)) + "," + branName.get(BranchMap.get(BranchMap.lowerKey(branKey))) + "," + BranchMap.lowerKey(branKey) + "\r\n");
						branKey = BranchMap.lowerKey(branKey);
					}
				}*/
			}catch(IOException  e){
				branOutFile.deleteOnExit() ;
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
			finally{
				branOutFileBW.close();
			}
			
		}catch(IOException  e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	
		try{		
			List<Map.Entry<String,Integer>> entries = new ArrayList<Map.Entry<String,Integer>>(comSales.entrySet());
	        Collections.sort(entries, new Comparator<Map.Entry<String,Integer>>() {
	        public int compare(
	            Entry<String,Integer> entry1, Entry<String,Integer> entry2) {
	        	return ((Integer)entry2.getValue()).compareTo((Integer)entry1.getValue());
	        }
	        });
			File comout = new File(args[0] + File.separator + "commodity.out");
			comout.createNewFile();
			FileWriter comoutFW = new FileWriter(comout);
			BufferedWriter comoutBW = new BufferedWriter(comoutFW);
			try{
				//int count = 0;
				for (Entry<String,Integer> s : entries) {
		            comoutBW.write(s.getKey() + "," + comName.get(s.getKey()) + "," + s.getValue() + "\r\n");
		        }
				/*for(int i = 0;i < comSales.size();i ++){
					if(comCodeSales.get(comCode.get(count)) != null){
						CommodityMap.put(comCodeSales.get(comCode.get(count)),comCode.get(count));//treemap にキー:売上金額 要素:商品コード
					}
					count ++;
				}
			//書き込み
				comoutBW.write(CommodityMap.get(CommodityMap.lastKey()) + "," + comName.get(CommodityMap.get(CommodityMap.lastKey())) + "," + CommodityMap.lastKey() + "\r\n");
				int comOutWrite = CommodityMap.lastKey();
				while(CommodityMap.lowerKey(comOutWrite) != null){
					comoutBW.write(CommodityMap.get(CommodityMap.lowerKey(comOutWrite)) + "," + comName.get(CommodityMap.get(CommodityMap.lowerKey(comOutWrite))) + "," + CommodityMap.lowerKey(comOutWrite) + "\r\n");
					comOutWrite = CommodityMap.lowerKey(comOutWrite);
				}*/
			}catch(IOException e){		
				comout.deleteOnExit() ;
				System.out.println("予期せぬエラーが発生しました");
				return;
			}	
			finally{
				comoutBW.close();
			}
	
		}catch(IOException  e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	}
}
