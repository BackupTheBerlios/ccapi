package ulsjabberd.xml;

public class PHashtable{

	Object[] key;
	Object[] value;


	PHashtable(){
		key=new Object[0];
		value=new Object[0];
	}

	public boolean contains(Object k){
		for(int i=0;i<key.length;i++){
			if(key[i].equals(k))return true;
		}
		return false;
	}

	public void put(Object k, Object v){
		int size=key.length;
		Object[] keyt=new Object[size+1];
		Object[] valuet=new Object[size+1];

		if(contains(k)){
			for(int i=0;i<size;i++){
				if(key[i].equals(k))value[i]=v;
			}
		}
		else{


			for(int i=0;i<size+1;i++){
				if(i<size){
					keyt[i]=key[i];
					valuet[i]=value[i];
				}
				else{
					keyt[i]=k;
						valuet[i]=v;
				}
			}

			key=keyt;
			value=valuet;
		}
	}

	public Object get(Object k){
		int size=key.length;

		for(int i=0;i<size;i++){
			if(key[i].equals(k)){
				return value[i];
			}
		}
		return null;
	}
	public int size(){
		return key.length;
	}


	public Object getKey(int i){
		try{
			return key[i];
		}catch(Exception e){

		}
		return null;
	}

}