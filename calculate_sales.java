//package jp.co.plusize.moriyama_naoki.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class calculate_sales {
	public static void main(String[] args){
		ArrayList<String> dataList = new ArrayList<Integer>();
		ArrayList<String> branchList1 = new ArrayList<String>();
		ArrayList<String> branchList2 = new ArrayList<String>();
		ArrayList<Integer> commodityList1 = new ArrayList<Integer>();
		ArrayList<String> commodityList2 = new ArrayList<String>();
		ArrayList<String> fileList = new ArrayList<String>();
		ArrayList<String> FileName = new ArrayList<String>();
		HashMap<String,Integer> map1 = new HashMap<String,Integer>();
		HashMap<String,Integer> map2 = new HashMap<String,Integer>();
		HashMap<String,Integer> map3 = new HashMap<String,Integer>();
		HashMap<String,Integer> map4 = new HashMap<String,Integer>();
		int x = 0,sumdata = 0,sumdata2 = 0,a = 0,b = 1,c = 2;
		
		try{                                     //1-1支店定義ファイルの読み込み
			File file1 = new File(args[0] + "\\branch.lst");
			FileReader fr1 = new FileReader(file1);
			BufferedReader br1 = new BufferedReader(fr1);
			String s1;
			x = 0;
			while(x<2){	
				s1 = br1.readLine();
				String[] branch = s1.split(",");//奇数:支店コード、偶数:支店
				if(x%2 == 0){
					//int data1 = Integer.parseInt(branch[x]);
					//branchList1.add(data1);
					map1.put(branch[x],0);
				}else{
					branchList2.add(branch[x]);
				}
				x++;
			}

			br1.close();
		}catch(IOException e){
			System.out.println("支店定義ファイルのフォーマットが不正です");
		}
		
		try{                                   //1-2商品定義ファイルの読み込み
			File file2 = new File(args[0] + "\\commodity.lst");
			FileReader fr2 = new FileReader(file2);
			BufferedReader br2 = new BufferedReader(fr2);
			String s2;
			x = 0;
			while(x<2){
				s2 = br2.readLine();
				String[] commodity = s2.split(",");//奇数:支店コード、偶数:支店名
				if(x%2 == 0){
					//int data2 = Integer.parseInt(commodity[x]);
					//commodityList1.add(data2);
					map2.put(commodity[x],0);
				}else{
					commodityList2.add(commodity[x]);
				}
				x++;

			}
			br2.close();
		}catch(IOException e){
			System.out.println("商品定義ファイルのフォーマットが不正です");
		}
		
	/*	try{              //3-1
			File filelist = new File(args[0]);
			File[] filelists = filelist.listFiles();
			for(int i=0;i<filelists.length;i++){
				if( filelists[i].getName().endsWith(".rcd")){
					FileName.add(filelists[i].getName());
				}
			}
			for(int i=0;i<FileName.size();i++){
				File file3 = new File(args[0] + "\\" + FileName.get(i));
				FileReader fr3 = new FileReader(file3);
				BufferedReader br3 = new BufferedReader(fr3);
				String s3;
				x=0;
				while(x>2){
					s3 = br3.readLine();
					int s = Integer.parseInt(s3);
					dataList.add(s);
					x++;
				}

				br3.close();
				                              //3-2 加算していく
				try{                        //支店コードが見つからなかったとき
					if(map1.get(dataList.get(a)) == null){
						throw new IOException("支店コードが不正です");
					}else{
						sumdata1 = map1.get(dataList.get(a)) + dataList.get(c);
					}
				}catch(IOException e){
					System.out.println(dataList.get(a) + "の支店コードが不正です");
				}

				try{                        //商品コードが見つからなかったとき
					if(map1.get(dataList.get(b)) == null){
						throw new IOException("商品コードが不正です");
					}else{
						sumdata2 = map2.get(dataList.get(b)) + dataList.get(c);
					}
				}catch(IOException e){
					System.out.println(dataList.get(b) + "の商品コードが不正です");
				}

				/*try{                        //４行以上あるとき
				}catch(IOException e){
					System.out.println(data + ".rcdのフォーマットが不正です");
				}*/
				
			/*	try{
					if(sumdata1 > 10 * 10){
						throw new IOException("合計金額が10桁を超えました");
					}
					if(sumdata2 > 10 * 10){
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
		*/
		
		File file3 = new File(args[0]);
		String[] files = file3.list();
		for(int i=0;i<files.length;i++){
			String xfiles = files[i];
			if(xfiles.endsWith("rcd") && xfiles.length() == 12){
				fileList.add(xfiles);
		}
		}//rcdファイル探し終わり
		
		//ファイル読み込み
		for(int i=0;i<fileList.size();i++){
			x=1;
			String X = String.valueOf(x);
			String s2;
			while(s2.length()<(8-X.length())){
				s2+="0";
			}
			s2=s2.concat(X);//s2にファイル名が
			
			File file4 = new File(args[0]+"\\"+s2+".rcd");
			FileReader fr4 = new FileReader(file4);
		 	BufferedReader br4 = new BufferedReader(fr4);
			String s4;
			while((s4 = br4.readLine()) != null){
				dataList.add(s4);
			}
			if(dataList.size()>2){
				//エラーを返す//4行以上のとき
			}
			int sum = Integer.parseInt(dataList.get(a));
			int sum2 = Integer.parseInt(dataList.get(c));
			sumdata = map1.get(sum) + sum2;//支店売上
			map3.put(dataList.get(a),sumdata);//支店コードと売上
			map4.put(dataList.get(b),sumdata);//商品コードと売上
			x++;
			dataList.clear();
			br4.close();
		}
		
		try{

			//降順に
			File file5 = new File(args[0] + "\\branch.out");
			FileWriter fw5 = new FileWriter(file5);
			BufferedWriter bw5 = new BufferedWriter(fw5);
			bw4.write(branchList1.get(0) + "," + branchList2.get(0) + "," + sumdata + "\r\n");
			
			File file6 = new File(args[0] + "\\commodity.out");
			FileWriter fw6 = new FileWriter(file6);
			BufferedWriter bw6 = new BufferedWriter(fw6);
		//	bw5.write(+","++","+\r\n);
			bw5.close();
			bw6.close();
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}
	}
}
