/* ══════════════════════════════════════════
   nav.js — shared navbar & drawer logic
══════════════════════════════════════════ */
zk.afterMount(function () {

  const hamburger = document.getElementById('hamburgerBtn');
  const drawer    = document.getElementById('drawer');
  const overlay   = document.getElementById('drawerOverlay');

  if (!hamburger || !drawer || !overlay) {
    console.log("No se encontraron elementos");
    return;
  }

  hamburger.addEventListener('click', () => {
    const isOpen = drawer.classList.toggle('open');
    overlay.classList.toggle('open', isOpen);
    hamburger.classList.toggle('open', isOpen);
  });

  overlay.addEventListener('click', closeDrawer);

  function closeDrawer() {
    drawer.classList.remove('open');
    overlay.classList.remove('open');
    hamburger.classList.remove('open');
  }

  window.closeDrawer = closeDrawer;

});

/* ══════════════════════════════════════════
   Toast helper
══════════════════════════════════════════ */
function showToast(msg) {
  let t = document.getElementById('toast');
  if (!t) {
    t = document.createElement('div');
    t.id = 'toast';
    t.className = 'toast';
    document.body.appendChild(t);
  }
  t.textContent = msg;
  t.classList.add('show');
  clearTimeout(t._timer);
  t._timer = setTimeout(() => t.classList.remove('show'), 2600);
}
