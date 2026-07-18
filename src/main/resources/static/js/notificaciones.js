document.addEventListener('DOMContentLoaded', function() {

    document.querySelectorAll('.btn-marcar-leida').forEach(function(btn) {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const id = this.dataset.id;
            const item = this.closest('.notificacion-item');

            fetch('/notificaciones/marcar-leida/' + id, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            })
            .then(response => response.text())
            .then(data => {
                if (data === 'success') {
                    item.classList.remove('no-leida');
                    this.remove();
                    actualizarContadores();
                }
            })
            .catch(error => console.error('Error:', error));
        });
    });

    const btnMarcarTodas = document.getElementById('btnMarcarTodasLeidas');
    if (btnMarcarTodas) {
        btnMarcarTodas.addEventListener('click', function(e) {
            e.preventDefault();
            if (!confirm('¿Marcar todas las notificaciones como leídas?')) return;

            fetch('/notificaciones/marcar-todas-leidas', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            })
            .then(() => {
                window.location.reload();
            })
            .catch(error => console.error('Error:', error));
        });
    }

    function actualizarContadores() {
        fetch('/notificaciones/contador')
            .then(response => response.text())
            .then(count => {
                const contadorGlobal = document.getElementById('contador-global');
                const contadorSidebar = document.getElementById('contador-notificaciones');
                const badgeCount = document.querySelector('.badge-count');

                if (contadorGlobal) {
                    if (parseInt(count) > 0) {
                        contadorGlobal.style.display = 'inline';
                        contadorGlobal.textContent = count;
                    } else {
                        contadorGlobal.style.display = 'none';
                    }
                }

                if (contadorSidebar) {
                    if (parseInt(count) > 0) {
                        contadorSidebar.textContent = count;
                    } else {
                        contadorSidebar.textContent = '0';
                    }
                }

                if (badgeCount) {
                    if (parseInt(count) > 0) {
                        badgeCount.style.display = 'inline';
                        badgeCount.textContent = count + ' no leídas';
                    } else {
                        badgeCount.style.display = 'none';
                    }
                }
            })
            .catch(() => {});
    }

    setInterval(actualizarContadores, 30000);

    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keyup', function(e) {
            if (e.key === 'Enter') {
                const query = this.value.trim();
                window.location.href = '/notificaciones?search=' + encodeURIComponent(query);
            }
        });
    }

    actualizarContadores();
});