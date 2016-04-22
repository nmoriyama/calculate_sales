//package jp.co.plusize.moriyama_naoki.calculate_sales;

import java.io.*;
import java.util.*;

public class umain {
	public static void main(String[] args){
		ArrayList<Integer> dataList = new ArrayList<Integer>();
		ArrayList<Integer> branchList1 = new ArrayList<Integer>();
		ArrayList branchList2 = new ArrayList();
		ArrayList<Integer> commodityList1 = new ArrayList<Integer>();
		ArrayList commodityList2 = new ArrayList();
		HashMap<String,Integer> map1=new HashMap<String,Integer>();
		HashMap<String,Integer> map2=new HashMap<String,Integer>();
		int x = 0,sumdata1 = 0,sumdata2 = 0,a = 0,b = 1,c = 2;
		
		try{                                     //1-1支店定義ファイルの読み込み
			File file1 = new File(args[0]+"\\branch.lst");
			FileReader fr1 = new FileReader(file1);
			BufferedReader br1 = new BufferedReader(fr1);
			String s1;
			x = 0;
			while((s1 = br1.readLine()) != null){	
				String[] branch = s1.split(",");//奇数:支店コード、偶数:支店名
				x++;
				if(x%2 == 0){
					int data1 = Integer.parseInt(branch[x]);
					branchList1.add(data1);
					map1.put(data1,0);
				}else{
					branchList2.add(branch[x]);
				}
			}
			br1.close();
		}catch(IOException e){
			System.out.println("支店定義ファイルのフォーマットが不正です");
		}
		
		try{                                   //1-2商品定義ファイルの読み込み
			File file2 = new File(args[0]+"\\commodity.lst");
			FileReader fr2 = new FileReader(file2);
			BufferedReader br2 = new BufferedReader(fr2);
			String s2;
			x = 0;
			while((s2 = br2.readLine()) != null){	
				String[] commodity = s2.split(",");//奇数:支店コード、偶数:支店名
				x++;
				if(x%2 == 0){
					int data2 = Integer.parseInt(commodity[x]);
					commodityList1.add(data2);
					map2.put(data2,0);
				}else{
					commodityList2.add(commodity[x]);
				}
			}
			br2.close();
		}catch(IOException e){
			System.out.println("商品定義ファイルのフォーマットが不正です");
		}
		
		try{              //3-1
			for(int data = 0;data<100;data++){//仮
				//このままだと８桁の数字ではないので文字列にする必要
				File file3 = new File(args[0]+"\\"+data+".rcd");
				if(!file3.exists()){
					int plot = data;
					plot++;
					File file4 = new File(args[0]+"\\"+plot+".rcd");
					if(file4.exists()){
						throw new IOException("連番になっていません");
					}
				}
				FileReader fr3 = new FileReader(file3);
				BufferedReader br3 = new BufferedReader(fr3);
				String s3;
				while((s3 = br3.readLine()) != null){
					int s = Integer.parseInt(s3);
					dataList.add(s);
				}
				                              //3-2 加算していく

				try{                        //支店コードが見つからなかったとき
					if(map1.get(dataList.get(0)) == null){
						throw new IOException("支店コードが不正です");
					}else{
						sumdata1=map1.get(dataList.get(a))+dataList.get(c);
					}
				}catch(IOException e){
					System.out.println(dataList.get(a)+"の支店コードが不正です");
				}

				try{                        //商品コードが見つからなかったとき
					if(map1.get(dataList.get(b)) == null){
						throw new IOException("商品コードが不正です");
					}else{
						sumdata2=map2.get(dataList.get(b))+dataList.get(c);
					}
				}catch(IOException e){
					System.out.println(dataList.get(b)+"の商品コードが不正です");
				}

				try{                        //４行以上あるとき
				}catch(IOException e){
					System.out.println(data+".rcdのフォーマットが不正です");
				}
				
				try{
					if(sumdata1 > 10*10){
						throw new IOException("合計金額が10桁を超えました");
					}
					if(sumdata2 > 10*10){
						throw new IOException("合計金額が10桁を超えました");
					}
				}catch(IOException e){
					System.out.println("合計金額が10桁を超えました");
				}
				a += 1;
				b += 1;
				c += 1;
			}
		}catch(IOException e){
			System.out.println("売上ファイル名が連番になっていません");
		}


		try{
			//降順に
			
			File file4 = new File(args[0]+"\\branch.out");
			FileWriter fw4 = new FileWriter(file4);
			BufferedWriter bw4 = new BufferedWriter(fw4);
			bw.write(+","++","+\r\n);
			
			File file5 = new File(args[0]+"\\commodity.out");
			FileWriter fw5 = new FileWriter(file5);
			BufferedWriter bw5 = new BufferedWriter(fw5);
			bw.write(+","++","+\r\n);
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}


	}

}
