
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class DynamicP_GreedyA {

	private static Scanner s;
	private static FileInputStream fis;
	private static ArrayList<Lion> hunting = new ArrayList<>();
	// https://www.geeksforgeeks.org/creating-tree-left-child-right-sibling-representation/
	// ref
	static class Lion {
		String name;
		Lion next, child, parent, left;
		int ability;
		boolean goHunt;
		int childA;
		ArrayList<Lion> children = new ArrayList<>();

		public Lion(String name, int ability) {
			this.name = name;
			next = null;
			child = null;
			parent = null;
			this.ability = ability;
			goHunt=false;
			childA = 0;
		}
	}

	public static int DP(Lion[] arr, Lion root) {
		int i = arr.length - 1;
		while (i > 0) { // sondan baslayarak arrayi dolasiyorum yani ilk baktiklarim leaf nodelar
			if (arr[i].parent.childA != 0) { // onceden belirlenmis ise geciyorum, belirlemeyi ise en sag kardeste yapiyorum
				i--; // yani en sag childa islem yapilmissa diger kardeste 
				continue; // (dogal olarak parentlari ayni oldugundan) tekrar hesaplama yapmiyorum
			}
			int sum1 = 0;
			int sum2 = 0;
			if ((arr[i].child == null && arr[i].next == null && arr[i].parent != null) // leaf node ya da o leveldaki tek kardes ise
					|| arr[i].left != null && arr[i].left.childA != 0) {
				if (arr[i].childA != 0) {
					sum1 += arr[i].childA; // zaten onceden belirlenmis, direkt oradan cekiyorum
				} else
					sum1 += arr[i].ability; // belirlenmemis ise direkt kendi abilitysini sum1'e ekliyorum
				Lion temp = arr[i].left; // solundaki kardesini temp tutuyorum, bu temp sonra eger varsa bir sola gecerek devam ediyor
				while (temp != null) {
					if (temp.childA != 0)
						sum1 += temp.childA; // yine ayni mantik, onceden belirlenmis ise aliyorum
					else
						sum1 += temp.ability; // belirlenmemis ise kendi sahip oldugu abilityi aliyorum
					temp = temp.left; // bir onceki kardes temp oluyor artik
				}

				sum2 = arr[i].parent.ability; // karsilastirma icin node'un parentinin abilitysini sum2'ye ekliyorum
				if (arr[i].left != null && arr[i].left.childA != 0) { // burada yaptigim ise torunlari eklemek

					Lion tempMain = arr[i];
					while (tempMain != null) {
						temp = tempMain.child;
						tempMain = tempMain.left;
						if (temp != null) {
							sum2 += getSumofChildren(temp);
						}
						temp = tempMain;
					}
				}
				int ab = Math.max(sum1, sum2); // max olani aliyorum ve 
				arr[i].parent.childA = ab; // childA seklinde Lion'in icinde tuttugum int degerini guncelliyorum
				i--;
				continue;
			}
			// bu if else iclerinde kod tekrarlamalari fazla fakat tek class kullandigimizden kodu fonksiyona bogmak istemedim
			// kodun devami da yukardakiyle benzer mantikta ilerliyor
			if (arr[i].child != null && arr[i].parent != null) { // hem cocugu hem de parenti olan nodelar icin, yani leaf node degil (treede ortalarda yer alanlar diyebiliriz)
				sum1 += arr[i].childA; // kendisi ve kardeslerinin toplami
				Lion temp = arr[i].left;
				while (temp != null) {
					if(temp.childA==0) // onceden belirlenmemis ise
						sum1+=temp.ability;
					else
						sum1 += temp.childA; // onceden belirlenmis ise ekle
					temp = temp.left;
				}
				sum2 += arr[i].parent.ability; // parent ability ekle
				Lion tempMain = arr[i];
				while (tempMain != null) {
					temp = tempMain.child;
					tempMain = tempMain.left; // en sola gel
					if (temp != null) {
						sum2 += getSumofChildren(temp); // cocuklarin toplamini al ( leaf nodesa yoktur zaten direkt 0 doner fonksiyondan)
					}
					temp = tempMain;
				}
				int ab = Math.max(sum1, sum2);
				arr[i].parent.childA = ab; // deger atandi
				i--;
			} 
			else { // yukaridakilerle ayni mantikta ilerliyor
				if (arr[i].childA != 0)
					sum1 += arr[i].childA;
				else {
					sum1 += arr[i].ability;
				}
				Lion temp = arr[i].left;
				while (temp != null) {
					if (temp.childA != 0)
						sum1 += temp.childA;
					else
						sum1 += temp.ability;
					temp = temp.left;
				}
				sum2 += arr[i].parent.ability;
				Lion tempMain = arr[i];
				while (tempMain != null) {
					temp = tempMain.child;
					tempMain = tempMain.left;
					if (temp != null) {
						sum2 += getSumofChildren(temp);
					}
					temp = tempMain;
				}
				int ab = Math.max(sum1, sum2);
				arr[i].parent.childA = ab;
				i--;
			}
		}
		int max = 0;
		Lion rootchild = root.child;
		while (rootchild.next != null) {
			rootchild = rootchild.next;
		}
		max=rootchild.parent.childA;
		return max;
	}
	
	
	public static int getSumofChildren(Lion node) { // onceden belirlenmis olan deger var ise childA degerini yok ise abilitysini aliyoruz
		int sum = 0;
		while (node != null) {
			if (node.childA != 0)
				sum += node.childA;
			else
				sum += node.ability;
			node = node.next;
		}
		return sum;
	}
	
	
	public static int Greedy(Lion[] arr) {
		
		int maxSum=0;
		int index=0;
		while(index<arr.length) { 
			if( arr[index].parent==null && goHuntChildren(arr[index].child)){ // root icin gerekli
				arr[index].goHunt=true; // alindigi icin true olarak degistiriyorum
				maxSum+=arr[index].ability;
				hunting.add(arr[index]);
			}
			else if(arr[index].parent.goHunt!=true) {
				if(goHuntChildren(arr[index].child)) { // cocuklari ve parenti gitmis mi kontrol ediyorum
					arr[index].goHunt=true;
					maxSum+=arr[index].ability;
					hunting.add(arr[index]);
				}
				else if(arr[index].child==null  ) { 
					maxSum+=arr[index].ability;
					hunting.add(arr[index]);
					arr[index].goHunt=true;
				}
			}
			index++;
		}
		System.out.println("\nGreedy Result:" + maxSum);
		System.out.println("-Lions that go hunting-");
		
		return maxSum;
	}
	public static Lion[] sortArrayMaxtoMin(ArrayList<Lion> list) {
		 // greedyde en yuksek ability'ye sahip olanlari ilk olarak alacagimdan
		// lionlari abilityleri max to min olavak seklinde siraliyorum
		Lion[] arr = new Lion[list.size()];
		int index=0;
		Lion max=list.get(0);
		int listI=1;
		while(list.size()>0) {
			boolean flag=true;
			while(flag) {
				if(max.ability<list.get(listI).ability)
					max=list.get(listI);
				listI++;
				if(listI==list.size())
					flag=false;	
			}
			list.remove(max); // arraye ekledigimi listten cikariyorum
			arr[index]=max; // arraye ekledim
			index++;
			listI=0; // basa donmek icin
			if(list.size()==0) // size 0'a dustugunde durdurmazsam exception veriyor (dogal olarak)
				break;
			max=list.get(listI); // max listten cikarilanda kalmasin diye guncelliyorum
		}
		return arr;
	}
	
	public static boolean goHuntChildren(Lion lion) { // dp ile baglantili oldugundan asagiya almadim
		while(lion!=null) {
			if(lion.goHunt==true) { // avlanmaya gidiyor ise
				return false;
			}
			lion=lion.next;
		}
		return true; // avlanmaya gitmiyor ise
	}
	
	

	private static void addSibling(Lion left, Lion right) { // kardes ekleme
		if (left == null)
			return;
		right.parent = left.parent; // sonradan kullanacagim icin left pointeri da kullanarak bagladim
		while (left.next != null)
			left = left.next;
		left.next = right;
		right.left = left;
	}

	private static void addChild(Lion node, Lion newLion) { // child eklemek icin
		if (node == null)
			return;
		newLion.parent = node;
		if (node.child != null)
			addSibling(node.child, newLion);
		else
			node.child = newLion;
	}

	public static void createHierarchy(Lion[] arr) {
		try {
			fis = new FileInputStream("lions_hierarchy.txt");
			s = new Scanner(fis);
			s.nextLine(); // ilk satiri geciyorum
			while (s.hasNext()) {
				String first = s.next();
				String second = s.next();
				String option = s.next();
				Lion firstL = null;
				Lion secondL = null;
				for (int i = 0; i < arr.length; i++) {
					if (arr[i].name.equals(first))
						firstL = arr[i];
					if (arr[i].name.equals(second))
						secondL = arr[i];
					if (firstL != null && secondL != null)
						break;
				}
				if (option.equalsIgnoreCase("Right-Sibling"))
					addSibling(firstL, secondL);

				else // option.equalsIgnoreCase("Left-Child")
					addChild(firstL, secondL);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Problem occured while reading the file..");
		}
	}

	public static void createAndStoreLions(Lion[] arr) {

		try {
			fis = new FileInputStream("hunting_abilities.txt");
			s = new Scanner(fis);
			s.nextLine(); // Node-name Hunting-ability atlamak icin
			int i = 0;
			while (s.hasNext()) {
				String name = s.next();
				arr[i] = new Lion(name, Integer.parseInt(s.next()));

				i++;
			}
		} catch (FileNotFoundException e) {
			System.out.println("Problem occured while reading the file..");
		}
	}

	private static int getSize() {

		int i = 0;
		try {
			fis = new FileInputStream("hunting_abilities.txt");
			s = new Scanner(fis);
			s.nextLine(); // Node-name Hunting-ability atlamak icin

			while (s.hasNext()) {
				i++;
				s.nextLine();
			}
			return i;
		} catch (FileNotFoundException e) {
			System.out.println("Problem occured while reading the file..");
		}
		return i;
	}

	// BFS
	// reference:
	// https://www.fatalerrors.org/a/leetcode-algorithm-problem-n-ary-tree-level-order-traversal-java-implementation.html
	public static List<List<Lion>> levelOrder(Lion root) {
		List<List<Lion>> result = new ArrayList<>();
		levelOrder(root, result, 0);
		return result;
	}

	// BFS
	public static void levelOrder(Lion root, List<List<Lion>> result, int level) {
		if (root == null) { 
			return;
		} // list icinde level sayisi kadar list olusuyor ve olusan bu listlerin sizelari o levelde kac tane
		// lion var ise ona gore belirleniyor.
		// en son mainde iclerinde dolasarak hepsini array icine atiyorums
		if (result.size() == level) {
			result.add(new ArrayList<>());
		}
		List<Lion> list = result.get(level);
		list.add(root);
		for (Lion n : root.children) {
			levelOrder(n, result, level + 1);
		}
	}
		
	public static void main(String[] args) {

		Lion[] arr = new Lion[getSize()];
		createAndStoreLions(arr);
		createHierarchy(arr);
		// To find root.
		int k = 0;
		Lion root = null;
		for (k = 0; k < arr.length; k++) {
			if (arr[k].parent == null) { // we know that root has no parent
				root = arr[k]; // root has found
				break;
			}
		}
		
		// to store all lions' children if exit.
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr.length; j++) {
				if (arr[j].parent == arr[i])
					arr[i].children.add(arr[j]);
			}
		}
		
		List<List<Lion>> levelO = levelOrder(root);

		Lion[] lionLevelArr = new Lion[arr.length]; // new array to store lions in top to bottom order
		int index = 0;
		for (List<Lion> lionList : levelO) { // listin icindeki her listte ilk depth, ikinci depth.. sonuncu depth seklinde listler olusuyor
			for (Lion lions : lionList) { // daha sonra onlari arraye atiyorum, bu sekilde array en alt sagdan en uste dogru siralaniyor
				lionLevelArr[index] = lions; // dp icin gerekli bir islem
				index++;
			}
		}

//		for (int i = 0; i < lionLevelArr.length; i++) {
//			System.out.print(lionLevelArr[i].name + " - ");
//		}
//		System.out.println("\n----------");
//		long startTime =  System.nanoTime();
		System.out.print("DP Result:" + DP(lionLevelArr, root));
//		long endTime =  System.nanoTime();
//		long time = endTime - startTime;
//		System.out.println("\nDP:" + time );
		ArrayList<Lion> toSortLions = new ArrayList<>();
		for (int i = 0; i < lionLevelArr.length; i++) {
			toSortLions.add(lionLevelArr[i]);
		}

		arr= sortArrayMaxtoMin(toSortLions);
//		System.out.println();
//		for (int i = 0; i < arr.length; i++) {
//			System.out.println(arr[i].name + " - " + arr[i].ability );
//		}
//		startTime =  System.nanoTime();
		Greedy(arr);
//		endTime =  System.nanoTime();
//		time = endTime - startTime;
//		System.out.println("Greedy:" + time );
		int i=0;
		for(Lion a:hunting) {
			System.out.print(a.name + " || ");
			i++;
			if(i%11==0)System.out.println();
		}
	}

}
