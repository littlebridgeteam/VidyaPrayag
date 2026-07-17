// Clock
function updateClock(){const n=new Date();document.getElementById('clock').textContent=`${String(n.getHours()).padStart(2,'0')}:${String(n.getMinutes()).padStart(2,'0')}`}
updateClock();setInterval(updateClock,1000);

// Theme
document.getElementById('themeBtn').addEventListener('click',()=>{document.documentElement.setAttribute('data-theme',document.documentElement.getAttribute('data-theme')==='dark'?'light':'dark')});

// Count-up
function countUp(el,target,d=1500){const s=performance.now();function t(n){const p=Math.min((n-s)/d,1);el.textContent=Math.round((1-Math.pow(1-p,3))*target);if(p<1)requestAnimationFrame(t)}requestAnimationFrame(t)}

// Gauge
function animGauge(id,pct){const el=document.getElementById(id);if(!el)return;const c=parseFloat(el.getAttribute('stroke-dasharray'));setTimeout(()=>{el.style.transition='stroke-dashoffset 1.8s cubic-bezier(0.4,0,0.2,1)';el.style.strokeDashoffset=c-(pct/100)*c},400)}

// Health ring
function animHealth(){const r=document.getElementById('mHealthRing'),v=document.getElementById('mHealthVal');if(!r)return;const c=parseFloat(r.getAttribute('stroke-dasharray'));setTimeout(()=>{r.style.transition='stroke-dashoffset 2s cubic-bezier(0.4,0,0.2,1)';r.style.strokeDashoffset=c-(82/100)*c},600);countUp(v,82,2000)}

// Sparkline
function drawSpark(){const svg=document.getElementById('mSpark');if(!svg)return;const data=[220,225,230,235,240,238,242,245,248],w=300,h=50;const mx=Math.max(...data),mn=Math.min(...data),rg=mx-mn||1;const pts=data.map((v,i)=>`${(i/(data.length-1))*w},${h-((v-mn)/rg)*(h-8)-4}`).join(' ');svg.innerHTML=`<defs><linearGradient id="sp" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#6C63FF" stop-opacity="0.3"/><stop offset="1" stop-color="#6C63FF" stop-opacity="0"/></linearGradient></defs><polygon points="0,${h} ${pts} ${w},${h}" fill="url(#sp)"/><polyline points="${pts}" fill="none" stroke="#6C63FF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" stroke-dasharray="600" stroke-dashoffset="600" style="animation:sparkDraw 2s ease-out forwards"/>`}

// Scroll reveal
const obs=new IntersectionObserver(e=>{e.forEach(en=>{if(en.isIntersecting){en.target.classList.add('visible');const f=en.target.querySelector('[data-fill]');if(f)f.style.width=f.dataset.fill+'%'}})},{threshold:0.1});
document.querySelectorAll('.reveal').forEach(el=>obs.observe(el));

// Dock
document.querySelectorAll('.m-dock__item').forEach(item=>{item.addEventListener('click',()=>{document.querySelectorAll('.m-dock__item').forEach(i=>i.classList.remove('m-dock__item--active'));item.classList.add('m-dock__item--active')})});

// Init
window.addEventListener('load',()=>{document.querySelectorAll('[data-countup]').forEach(el=>countUp(el,parseInt(el.dataset.countup)));animGauge('mGaugeAtt',94);animGauge('mGaugeFees',87);animHealth();drawSpark()});
