// ══ VIDYA PRAYAG — PARENT DASHBOARD ENGINE ══

// Theme
document.getElementById('themeToggle').addEventListener('click',()=>{
  const h=document.documentElement;h.setAttribute('data-theme',h.getAttribute('data-theme')==='dark'?'light':'dark');
});

// Count-up
function countUp(el,target,d=1500){const s=performance.now();function t(n){const p=Math.min((n-s)/d,1);el.textContent=Math.round((1-Math.pow(1-p,3))*target);if(p<1)requestAnimationFrame(t)}requestAnimationFrame(t)}

// Gauge
function animGauge(id,pct){const el=document.getElementById(id);if(!el)return;const c=parseFloat(el.getAttribute('stroke-dasharray'));setTimeout(()=>{el.style.transition='stroke-dashoffset 1.8s cubic-bezier(0.4,0,0.2,1)';el.style.strokeDashoffset=c-(pct/100)*c},400)}

// Progress ring
function animProgress(){const r=document.getElementById('progressRing'),v=document.getElementById('progressValue');if(!r)return;const c=parseFloat(r.getAttribute('stroke-dasharray'));setTimeout(()=>{r.style.transition='stroke-dashoffset 2s cubic-bezier(0.4,0,0.2,1)';r.style.strokeDashoffset=c-(84/100)*c},600);countUp(v,84,2000)}

// Particles
function createParticles(){const c=document.getElementById('particles');if(!c)return;for(let i=0;i<25;i++){const p=document.createElement('div');const s=Math.random()*4+2;p.style.cssText=`position:absolute;width:${s}px;height:${s}px;border-radius:50%;background:rgba(126,94,255,${Math.random()*0.3+0.1});left:${Math.random()*100}%;top:${Math.random()*100}%;animation:pf ${Math.random()*10+10}s infinite linear;animation-delay:-${Math.random()*10}s`;c.appendChild(p)}}
const ps=document.createElement('style');ps.textContent=`@keyframes pf{0%{transform:translate(0,0);opacity:0}10%{opacity:1}90%{opacity:1}100%{transform:translate(${Math.random()*100-50}px,-100vh);opacity:0}}`;document.head.appendChild(ps);createParticles();

// Parallax
document.addEventListener('mousemove',(e)=>{
  const mx=(e.clientX/window.innerWidth-0.5)*2,my=(e.clientY/window.innerHeight-0.5)*2;
  const orb=document.getElementById('heroOrb');if(orb)orb.style.transform=`translate(${mx*20}px,${my*20}px)`;
  document.querySelectorAll('.aurora-blob').forEach((b,i)=>{const f=(i+1)*8;b.style.marginLeft=`${mx*f}px`;b.style.marginTop=`${my*f}px`});
});
window.addEventListener('scroll',()=>{const sy=window.scrollY;const hero=document.getElementById('hero');if(hero)hero.style.transform=`translateY(${sy*0.15}px)`});

// Magnetic
document.querySelectorAll('.btn-magnetic, .priority__cta').forEach(btn=>{
  btn.addEventListener('mousemove',(e)=>{const r=btn.getBoundingClientRect();btn.style.transform=`translate(${(e.clientX-r.left-r.width/2)*0.2}px,${(e.clientY-r.top-r.height/2)*0.2}px) scale(1.04)`});
  btn.addEventListener('mouseleave',()=>{btn.style.transform=''});
});

// Ripple
function rippleClick(e){const t=e.currentTarget,r=t.getBoundingClientRect(),rip=document.createElement('span');rip.className='ripple';const s=Math.max(r.width,r.height);rip.style.width=rip.style.height=`${s}px`;rip.style.left=`${e.clientX-r.left-s/2}px`;rip.style.top=`${e.clientY-r.top-s/2}px`;t.appendChild(rip);setTimeout(()=>rip.remove(),600)}
window.rippleClick=rippleClick;

// Tilt
document.querySelectorAll('[data-tilt]').forEach(card=>{
  card.addEventListener('mousemove',(e)=>{const r=card.getBoundingClientRect();const x=(e.clientX-r.left)/r.width-0.5,y=(e.clientY-r.top)/r.height-0.5;card.style.transform=`translateY(-4px) perspective(800px) rotateY(${x*6}deg) rotateX(${-y*6}deg)`});
  card.addEventListener('mouseleave',()=>{card.style.transform=''});
});

// Priority expand
function togglePriority(){document.getElementById('priorityCard').classList.toggle('expanded')}
window.togglePriority=togglePriority;

// Notif
function toggleNotif(){document.getElementById('notifPanel').classList.toggle('active')}
window.toggleNotif=toggleNotif;
document.getElementById('notifBtn').addEventListener('click',toggleNotif);

// Dock
const dockItems=document.querySelectorAll('.dock__item'),dockPill=document.getElementById('dockPill');
function moveDockPill(item){const r=item.getBoundingClientRect(),d=item.closest('.dock').getBoundingClientRect();dockPill.style.opacity='1';dockPill.style.width=`${r.width}px`;dockPill.style.height=`${r.height}px`;dockPill.style.left=`${r.left-d.left}px`;dockPill.style.top=`${r.top-d.top}px`}
dockItems.forEach(item=>{item.addEventListener('mouseenter',()=>moveDockPill(item));item.addEventListener('click',()=>{dockItems.forEach(i=>i.classList.remove('dock__item--active'));item.classList.add('dock__item--active')})});
document.getElementById('dock').addEventListener('mouseleave',()=>{const a=document.querySelector('.dock__item--active');if(a)moveDockPill(a)});

// Child pill switch
document.querySelectorAll('.child-pill').forEach(pill=>{pill.addEventListener('click',()=>{document.querySelectorAll('.child-pill').forEach(p=>p.classList.remove('child-pill--active'));pill.classList.add('child-pill--active')})});

// Scroll reveal
const obs=new IntersectionObserver(e=>{e.forEach(en=>{if(en.isIntersecting){en.target.classList.add('visible');const f=en.target.querySelector('[data-fill]');if(f)f.style.width=f.dataset.fill+'%'}})},{threshold:0.1});
document.querySelectorAll('.bento-card, .priority, .health-bars').forEach(el=>{el.classList.add('reveal');obs.observe(el)});

// AI confidence
setTimeout(()=>{const c=document.getElementById('aiConfidence');if(c)c.style.width='94%'},800);

// Mark bars
const markObs=new IntersectionObserver(e=>{e.forEach(en=>{if(en.isIntersecting){const f=en.target;f.style.width=f.dataset.fill+'%'}})},{threshold:0.3});
document.querySelectorAll('.mark-item__fill').forEach(f=>markObs.observe(f));

// Health bars
const barObs=new IntersectionObserver(e=>{e.forEach(en=>{if(en.isIntersecting){en.target.style.width=en.target.dataset.fill+'%'}})},{threshold:0.3});
document.querySelectorAll('.health-bar-fill').forEach(f=>barObs.observe(f));

// Init
window.addEventListener('load',()=>{
  document.querySelectorAll('[data-countup]').forEach(el=>countUp(el,parseInt(el.dataset.countup)));
  animGauge('gaugeAttendance',96);
  animProgress();
  const a=document.querySelector('.dock__item--active');if(a)setTimeout(()=>moveDockPill(a),100);
});

// Keyboard
document.addEventListener('keydown',(e)=>{if((e.metaKey||e.ctrlKey)&&e.key==='k'){e.preventDefault();document.getElementById('searchInput').focus()}});
