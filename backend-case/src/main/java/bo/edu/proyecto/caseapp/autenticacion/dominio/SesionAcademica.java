package bo.edu.proyecto.caseapp.autenticacion.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "sesiones_academicas")
public class SesionAcademica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 128)
    private String token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioAcademico usuario;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    @Column(name = "expira_en", nullable = false)
    private LocalDateTime expiraEn;

    @Column(name = "revocado_en")
    private LocalDateTime revocadoEn;

    protected SesionAcademica() {
    }

    public SesionAcademica(String token, UsuarioAcademico usuario, LocalDateTime expiraEn) {
        this.token = token;
        this.usuario = usuario;
        this.expiraEn = expiraEn;
    }

    @PrePersist
    void antesDeCrear() {
        if (creadoEn == null) {
            creadoEn = LocalDateTime.now();
        }
    }

    public void revocar(LocalDateTime momento) {
        if (revocadoEn == null) {
            revocadoEn = momento;
        }
    }

    public boolean estaVigente(LocalDateTime momento) {
        return revocadoEn == null && expiraEn.isAfter(momento) && usuario.isActivo();
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public UsuarioAcademico getUsuario() {
        return usuario;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public LocalDateTime getExpiraEn() {
        return expiraEn;
    }

    public LocalDateTime getRevocadoEn() {
        return revocadoEn;
    }
}
