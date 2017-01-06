package me.guigarciazinho.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import me.guigarciazinho.models.PlayerGame;
import me.guigarciazinho.principal.Main;


public class MySQL {
	private Main plugin;
	private Connection con = null;
	private Statement s;
	private String user;
	private String senha;
	private String urlconf;
	private String dbname;
	private PlayerGame player = null;
	private int resultado;
	
	public MySQL(Main main){
		plugin = main;
		user = plugin.getConfig().getString("User");
		senha = plugin.getConfig().getString("Senha");
		urlconf = plugin.getConfig().getString("Urlconf");
		dbname = plugin.getConfig().getString("Dbname");
	}
	
	public void conectar(){
		final String driver = "com.mysql.jdbc.Driver";
		final String url = "jdbc:mysql://" +urlconf +":3306/" + dbname;
		
		try{
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, senha);
		}catch(ClassNotFoundException erro){
			System.out.println("Driver não encontrado." + erro.toString());
		}catch(SQLException erro){
			System.out.println("Ocorreu um erro ao tentar se conectar com o banco de dados." + erro.toString());
		}
	}
		
	
	
	public void criarTabela(){
		
		try{
			conectar();
			s = con.createStatement();
			s.executeUpdate("CREATE TABLE IF NOT EXISTS KamiX1("
                          +" id VARCHAR(36) not null,"
                          +" nick VARCHAR(16) not null,"
                          +" vitorias MEDIUMINT,"
                          +" derrotas MEDIUMINT,"
                          +" PRIMARY KEY(id)"
						  +")DEFAULT CHARSET = utf8;");
			s.close();
			con.close();
		
		}catch(SQLException erro){
			System.out.println("Ocorreu um erro ao tentar se conectar com o banco de dados." + erro.toString());
	}
	}
	public int getVitorias(UUID id){
		try{
			conectar();
			  s = con.createStatement();
			    ResultSet rs = s.executeQuery("SELECT * FROM KamiX1 WHERE id = '"+id+"'");
			    if(rs.next()){
			    	resultado = rs.getInt("vitorias");
			        s.close();
			   	    rs.close();
			   	    con.close();
			   	    return resultado;
			    }else{
			    System.out.println("Sucesso");
			    s.close();
			    rs.close();
			    con.close();
			    return 0;
			    }
		}catch(SQLException e){
			System.out.println(e.toString());
		}
		return 0;
	}
	
	public int getDerrotas(UUID id){
		try{
			conectar();
			  s = con.createStatement();
			    ResultSet rs = s.executeQuery("SELECT * FROM KamiX1 WHERE id = '"+id+"'");
			    if(rs.next()){
			    	resultado = rs.getInt("derrotas");
			        s.close();
			   	    rs.close();
			   	    con.close();
			   	    return resultado;
			    }else{
			    System.out.println("Sucesso");
			    s.close();
			    rs.close();
			    con.close();
			    return 0;
			    }
		}catch(SQLException e){
			System.out.println(e.toString());
		}
		return 0;
	}
	
	public void getDados(UUID id){
		player = plugin.game.getPlayerGame(id);
		player.setCarregado();
		player.setDerrotas(getDerrotas(id));
		player.setVitorias(getVitorias(id));
		System.out.println("Player salvo.");
	}
	
	
	public boolean gerenciarVencedor(UUID idVencedor, String nomeVencedor){
		try{
		conectar();
	    s = con.createStatement();
	    ResultSet rs = s.executeQuery("SELECT * FROM KamiX1 WHERE id = '"+idVencedor+"'");
	    if(rs.next()){
	    	atualizarDadosVencedor(idVencedor);
	        s.close();
	   	    rs.close();
	   	    con.close();
	    	return false;
	    }else{
	    String sql ="insert into KamiX1"
	    		       +" (id, nick, vitorias, derrotas)"
	    		       +" values"
	    		       +" ('"+ idVencedor+"', '"+nomeVencedor+"', '1', '0');";
	    s.executeUpdate(sql);
	    System.out.println("Sucesso");
	    s.close();
	    rs.close();
	    con.close();
	    return true;
	    }
		}catch(SQLException e){
			System.out.println(e.toString());
			return false;
		}
	}
	
	public boolean gerenciarPerdedor(UUID idPerdedor, String nomePerdedor){
		try{
		conectar();
	    s = con.createStatement();
	    ResultSet rs = s.executeQuery("SELECT * FROM KamiX1 WHERE id = '"+idPerdedor+"'");
	    if(rs.next()){
	    	atualizarDadosPerdedor(idPerdedor);
	        s.close();
	   	    rs.close();
	   	    con.close();
	    	return false;
	    }else{
	    String sql ="insert into KamiX1"
	    		       +" (id, nick, vitorias, derrotas)"
	    		       +" values"
	    		       +" ('"+ idPerdedor+"', '"+nomePerdedor+"', '0', '1');";
	    s.executeUpdate(sql);
	    System.out.println("Sucesso");
	    s.close();
	    rs.close();
	    con.close();
	    return true;
	    }
		}catch(SQLException e){
			System.out.println(e.toString());
			return false;
		}
	}
		
	
	public void atualizarDadosVencedor(UUID idVencedor){
		try{
		conectar();
	    s = con.createStatement();
	    String sql ="UPDATE KamiX1"
	    		       +" SET vitorias = vitorias + 1 "
	    		       +"WHERE id = '"+ idVencedor+"';";
	    s.executeUpdate(sql);
	    s.close();
	    con.close();
	    System.out.println("Sucesso");
	    player = plugin.game.getPlayerGame(idVencedor);
	    if(player.getCarregado()){
	    	player.setVitorias(player.getVitorias() + 1);
	    }
		  
		}catch(SQLException e){
			System.out.println(e.toString());
			
		}
	}
		
		public void atualizarDadosPerdedor(UUID idPerdedor){
			try{
			conectar();
		    s = con.createStatement();
		    String sql ="UPDATE KamiX1"
		    		       +" SET vitorias = vitorias + 1 "
		    		       +"WHERE id = '"+ idPerdedor+"';";
		    s.executeUpdate(sql);
		    s.close();
		    con.close();
		    player = plugin.game.getPlayerGame(idPerdedor);
		    if(player.getCarregado()){
		    	player.setVitorias(player.getDerrotas() + 1);
		    }
		    System.out.println("Sucesso");
			  
			}catch(SQLException e){
				System.out.println(e.toString());
			}
	}
		
}
