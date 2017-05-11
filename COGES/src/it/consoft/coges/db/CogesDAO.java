package it.consoft.coges.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.consoft.coges.model.Costo;
import it.consoft.coges.model.DescrizioneCosto;
import it.consoft.coges.model.RettificaGenerale;

public class CogesDAO {
	
	private DBConnect connettore;
	private final int tipoContabilitaGenerale = 1;
	private final int tipoRettificheCoges = 2;
	private final int tipoContabilitaCoges = 3;
	
	public CogesDAO(){
		
		connettore = new DBConnect();
	}
	
	public List<DescrizioneCosto> caricaAnagraficaCosti(){
		
		try{
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return null;
			}
			
			List<DescrizioneCosto> ritorno = new ArrayList<DescrizioneCosto>();
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Anagrafica_Costi}");
			
			ResultSet rs = stm.executeQuery(); 
			
			while (rs.next()) {
				ritorno.add(new DescrizioneCosto(rs.getString("Codice_Costo"), rs.getInt("Tipologia_Costo"), rs.getString("Descrizione_Costo").toLowerCase()));
			}
			
			rs.close();
			stm.close();
			conn.close();
			
			conn = null;
			
			return ritorno;
			
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
		
	}

	public List<Costo> caricaCostiContabilitaGenerale(Timestamp dataInizio, Timestamp dataFine, String societa){
		
		try{
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return null;
			}
			
			List<Costo> ritorno = new ArrayList<Costo>();
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Piano_Dei_Conti_Con_Amount(?, ?, ?)}");
			stm.setTimestamp(1, dataInizio);
			stm.setTimestamp(2, dataFine);
			stm.setString(3, societa);
			
			ResultSet rs = stm.executeQuery(); 
			
			while (rs.next()) {
				ritorno.add(new Costo(rs.getString("code"), rs.getString("description"), rs.getDouble("amount"), tipoContabilitaGenerale));
			}
			
			rs.close();
			stm.close();
			conn.close();
			
			conn = null;
			
			return ritorno;
			
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public List<Costo> caricaCostiContabilitaCoges(Timestamp dataInizio, Timestamp dataFine, String societa) {
		
		try{
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return null;
			}
			
			List<Costo> ritorno = new ArrayList<Costo>();
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Contabilita_Coges(?, ?, ?)}");
			stm.setTimestamp(1, dataInizio);
			stm.setTimestamp(2, dataFine);
			stm.setString(3, societa);
			
			ResultSet rs = stm.executeQuery(); 
			
			while (rs.next()) {
				ritorno.add(new Costo(rs.getString("code"), rs.getString("description"), rs.getDouble("amount"), tipoContabilitaCoges));
			}
			
			rs.close();
			stm.close();
			conn.close();
			
			conn = null;
			
			return ritorno;
			
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public List<Costo> caricaRettificheCoges(Timestamp dataInizio, Timestamp dataFine, String societa) {
		try{
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return null;
			}
			
			List<Costo> ritorno = new ArrayList<Costo>();
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Costi_Indiretti(?, ?, ?)}");
			stm.setTimestamp(1, dataInizio);
			stm.setTimestamp(2, dataFine);
			stm.setString(3, societa);
			
			ResultSet rs = stm.executeQuery(); 
			
			while (rs.next()) {
				ritorno.add(new Costo(rs.getString("code"), rs.getString("description"), rs.getDouble("amount"), tipoRettificheCoges));
			}
			
			rs.close();
			stm.close();
			conn.close();
			
			conn = null;
			
			return ritorno;
			
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Costo> caricaRettificheIntercompany(Timestamp dataInizio, Timestamp dataFine, String societa) {
		try{
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return null;
			}
			
			List<Costo> ritorno = new ArrayList<Costo>();
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Rettifiche_Intercompany(?, ?, ?)}");
			stm.setTimestamp(1, dataInizio);
			stm.setTimestamp(2, dataFine);
			stm.setString(3, societa);
			
			ResultSet rs = stm.executeQuery(); 
			
			while (rs.next()) {
				ritorno.add(new Costo(rs.getString("code"), rs.getString("description"), rs.getDouble("amount"), tipoRettificheCoges));
			}
			
			rs.close();
			stm.close();
			conn.close();
			
			conn = null;
			
			return ritorno;
			
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Costo> caricaRettificheCompensi(Timestamp dataInizio, Timestamp dataFine, String societa) {
		try{
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return null;
			}
			
			List<Costo> ritorno = new ArrayList<Costo>();
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Rettifiche_Compensi(?, ?, ?)}");
			stm.setTimestamp(1, dataInizio);
			stm.setTimestamp(2, dataFine);
			stm.setString(3, societa);
			
			ResultSet rs = stm.executeQuery(); 
			
			while (rs.next()) {
				ritorno.add(new Costo(rs.getString("code"), rs.getString("description"), rs.getDouble("amount"), tipoRettificheCoges));
			}
			
			rs.close();
			stm.close();
			conn.close();
			
			conn = null;
			
			return ritorno;
			
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean eliminaTutteRettifiche(){
		try{
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return false;
			}
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Rimozione_Tutte_Rettifiche_Contabilita_Generale}");
			
			stm.executeUpdate();
			
			stm.close();
			conn.close();
			
			conn = null;
			
			return true;
			
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public boolean eliminaRettifiche(String societa) {
		try{
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return false;
			}
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Rimozione_Rettifiche_Contabilita_Generale(?)}");
			stm.setString(1, societa);
			
			stm.executeUpdate();
			
			stm.close();
			conn.close();
			
			conn = null;
			
			return true;
			
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public boolean InserisciRettifica(Timestamp data, String numeroConto, double importo, String societa) {
		try{
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return false;
			}
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Aggiornamento_Rettifiche_Contabilita_geenrale(?, ?, ?, ?)}");
			stm.setTimestamp(1, data);
			stm.setString(2, numeroConto);
			stm.setDouble(3, importo);
			stm.setString(4, societa);
			
			stm.executeUpdate(); 

			stm.close();
			conn.close();
			
			conn = null;
			
			return true;
			
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	
	public List<RettificaGenerale> caricaRettificheContabilitaGenerale(String societa) {
		try{
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return null;
			}
			
			List<RettificaGenerale> rettifiche = new ArrayList<RettificaGenerale>();
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Lettura_Rettifiche_Contabilita_Generale(?)}");
			stm.setString(1, societa);
			
			ResultSet rs = stm.executeQuery(); 
			
			while (rs.next()) {
				RettificaGenerale r = new RettificaGenerale(rs.getTimestamp("Data"), rs.getString("Codice_Conto"), rs.getDouble("importo"));
				rettifiche.add(r);
			}
			
			rs.close();
			stm.close();
			conn.close();
			
			conn = null;
			
			return rettifiche;
			
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean eliminaSingolaRettifica(Timestamp data, String codiceCosto, String societa){
		try{
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return false;
			}
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Rimozione_Singola_Rettifica(?, ?, ?)}");
			
			stm.setTimestamp(1, data);
			stm.setString(2, codiceCosto);
			stm.setString(3, societa);
			
			stm.executeUpdate();
			
			stm.close();
			conn.close();
			
			conn = null;
			
			return true;
			
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean modificaRettifica(Timestamp data, String codiceCosto, double importo, String societa){
		try{
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return false;
			}
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Modifica_Singola_Rettifica(?, ?, ?, ?)}");
			
			stm.setTimestamp(1, data);
			stm.setString(2, codiceCosto);
			stm.setDouble(3, importo);
			stm.setString(4, societa);
			
			stm.executeUpdate();
			
			stm.close();
			conn.close();
			
			conn = null;
			
			return true;
			
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public Collection<Costo> caricaContiPerDialogModifiche(){
		try{
			Connection conn = connettore.getConnection();
			
			Collection<Costo> conti = new ArrayList<Costo>();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return null;
			}
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Piano_Dei_Conti}");
		
			ResultSet rs = stm.executeQuery(); 
			
			while (rs.next()) {
				conti.add(new Costo(rs.getString("Number")));
			}
			
			rs.close();
			stm.close();
			conn.close();
			conn = null;
			
			return conti;
			
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public int controllaRimuoviAssociazione(String numeroConto){
		try{
			Connection conn = connettore.getConnection();
			
			int righeTrovate = 0;
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return 0;
			}
			
			PreparedStatement stm = conn.prepareStatement("{call dbo.Ema_Trova_Associazioni(?)}");
			
			stm.setString(1, numeroConto);
		
			ResultSet rs = stm.executeQuery(); 
			
			while (rs.next()) {
				righeTrovate++;
			}
			
			rs.close();
			stm.close();
			
			PreparedStatement stmUno = conn.prepareCall("{call dbo.Ema_Rimuovi_Associazioni(?)}");
			
			stmUno.setString(1, numeroConto);
			
			stmUno.executeUpdate();
			
			stmUno.close();
			conn.close();
			conn = null;
			
			return righeTrovate;
			
		} catch (Exception e){
			e.printStackTrace();
			return 0;
		}
	}
	
	public boolean inserisciAssociazione(String numeroConto, int tipoConto, String categoria){
		try{
			
			Connection conn = connettore.getConnection();
			
			if(conn == null){
				System.out.println("La connessione non è stata stabilita");
				return false;
			}
			
			PreparedStatement stm = conn.prepareCall("{call dbo.Ema_Inserisci_Associazione(?, ?, ?)}");
			
			stm.setString(1, numeroConto);
			stm.setInt(2, tipoConto);
			stm.setString(3, categoria);
			
			stm.executeUpdate();
			
			stm.close();
			conn.close();
			conn = null;
			
			return true;
			
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
