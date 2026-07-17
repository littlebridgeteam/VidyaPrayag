// ═══════════════════════════════════════════════════════════
// VIDYA PRAYAG — PREMIUM DASHBOARD ENGINE
// ═══════════════════════════════════════════════════════════

// ── CLOCK ──────────────────────────────────────────────────
function updateClock(){
  const now=new Date();
  const h=String(now.getHours()).padStart(2,'0');
  const m=String(now.getMinutes()).padStart(2,'0');
  document.getElementById('clock').textContent=`${h}:${m}`;
  const days=['Sun','Mon','Tue','Wed','Thu','Fri','Sat'];
  const months=['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
  document.getElementById('dateStr').textContent=`${days[now.getDay()]}, ${now.getDate()} ${months[now.getMonth()]}`;
}
updateClock();setInterval(updateClock,1000);

// ── THEME TOGGLE ───────────────────────────────────────────
const themeToggle=document.getElementById('themeToggle');
themeToggle.addEventListener('click',()=>{
  const html=document.documentElement;
  const cur=html.getAttribute('data-theme');
  html.setAttribute('data-theme',cur==='dark'?'light':'dark');
});

// ── COUNT-UP ANIMATION ─────────────────────────────────────
function countUp(el,target,duration=1500){
  const start=performance.now();
  function tick(now){
    const p=Math.min((now-start)/duration,1);
    const eased=1-Math.pow(1-p,3);
    el.textContent=Math.round(eased*target);
    if(p<1)requestAnimationFrame(tick);
  }
  requestAnimationFrame(tick);
}

// ── GAUGE ANIMATION ────────────────────────────────────────
function animateGauge(id,percent){
  const el=document.getElementById(id);
  if(!el)return;
  const circumference=parseFloat(el.getAttribute('stroke-dasharray'));
  const offset=circumference-(percent/100)*circumference;
  setTimeout(()=>{el.style.transition='stroke-dashoffset 1.8s cubic-bezier(0.4,0,0.2,1)';el.style.strokeDashoffset=offset},300);
}

// ── HEALTH RING ────────────────────────────────────────────
function animateHealthRing(){
  const ring=document.getElementById('healthRing');
  const val=document.getElementById('healthValue');
  if(!ring)return;
  const circ=parseFloat(ring.getAttribute('stroke-dasharray'));
  const score=82;
  const offset=circ-(score/100)*circ;
  setTimeout(()=>{ring.style.transition='stroke-dashoffset 2s cubic-bezier(0.4,0,0.2,1)';ring.style.strokeDashoffset=offset},500);
  countUp(val,score,2000);
}

// ── SPARKLINE ──────────────────────────────────────────────
function drawSparkline(svgId,data,color){
  const svg=document.getElementById(svgId);
  if(!svg)return;
  const w=300,h=60;
  const max=Math.max(...data),min=Math.min(...data);
  const range=max-min||1;
  const pts=data.map((v,i)=>{
    const x=(i/(data.length-1))*w;
    const y=h-((v-min)/range)*(h-10)-5;
    return`${x},${y}`;
  }).join(' ');
  const areaPts=`0,${h} ${pts} ${w},${h}`;
  svg.innerHTML=`
    <defs>
      <linearGradient id="spGrad${svgId}" x1="0" y1="0" x2="0" y2="1">
        <stop offset="0" stop-color="${color}" stop-opacity="0.3"/>
        <stop offset="1" stop-color="${color}" stop-opacity="0"/>
      </linearGradient>
    </defs>
    <polygon points="${areaPts}" fill="url(#spGrad${svgId})"/>
    <polyline points="${pts}" fill="none" stroke="${color}" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
      stroke-dasharray="600" stroke-dashoffset="600" style="animation:sparkDraw 2s ease-out forwards"/>
  `;
}

// Add sparkline keyframe
const sparkStyle=document.createElement('style');
sparkStyle.textContent=`@keyframes sparkDraw{to{stroke-dashoffset:0}}`;
document.head.appendChild(sparkStyle);

// ── PARTICLES ──────────────────────────────────────────────
function createParticles(){
  const container=document.getElementById('particles');
  if(!container)return;
  for(let i=0;i<30;i++){
    const p=document.createElement('div');
    const size=Math.random()*4+2;
    p.style.cssText=`position:absolute;width:${size}px;height:${size}px;border-radius:50%;background:rgba(108,99,255,${Math.random()*0.3+0.1});left:${Math.random()*100}%;top:${Math.random()*100}%;animation:particleFloat ${Math.random()*10+10}s infinite linear;animation-delay:-${Math.random()*10}s`;
    container.appendChild(p);
  }
}
const pStyle=document.createElement('style');
pStyle.textContent=`@keyframes particleFloat{0%{transform:translate(0,0);opacity:0}10%{opacity:1}90%{opacity:1}100%{transform:translate(${Math.random()*100-50}px,-100vh);opacity:0}}`;
document.head.appendChild(pStyle);
createParticles();

// ── AURORA RAYS ────────────────────────────────────────────
function createRays(){
  const container=document.getElementById('auroraRays');
  if(!container)return;
  for(let i=0;i<3;i++){
    const ray=document.createElement('div');
    ray.style.cssText=`position:absolute;width:2px;height:100%;background:linear-gradient(to bottom,transparent,rgba(108,99,255,0.06),transparent);left:${20+i*30}%;animation:rayMove ${8+i*4}s infinite ease-in-out;animation-delay:-${i*2}s`;
    container.appendChild(ray);
  }
}
const rStyle=document.createElement('style');
rStyle.textContent=`@keyframes rayMove{0%,100%{transform:translateX(0) rotate(0)}50%{transform:translateX(40px) rotate(3deg)}}`;
document.head.appendChild(rStyle);
createRays();

// ── PARALLAX ───────────────────────────────────────────────
let mouseX=0,mouseY=0;
document.addEventListener('mousemove',(e)=>{
  mouseX=(e.clientX/window.innerWidth-0.5)*2;
  mouseY=(e.clientY/window.innerHeight-0.5)*2;
  const orb=document.getElementById('heroOrb');
  if(orb)orb.style.transform=`translate(${mouseX*20}px,${mouseY*20}px)`;
  const blobs=document.querySelectorAll('.aurora-blob');
  blobs.forEach((b,i)=>{
    const factor=(i+1)*8;
    b.style.marginLeft=`${mouseX*factor}px`;
    b.style.marginTop=`${mouseY*factor}px`;
  });
});

// Scroll parallax
let scrollY=0;
window.addEventListener('scroll',()=>{
  scrollY=window.scrollY;
  const hero=document.getElementById('hero');
  if(hero)hero.style.transform=`translateY(${scrollY*0.15}px)`;
});

// ── MAGNETIC BUTTONS ───────────────────────────────────────
document.querySelectorAll('.btn-magnetic, .priority__cta').forEach(btn=>{
  btn.addEventListener('mousemove',(e)=>{
    const rect=btn.getBoundingClientRect();
    const x=e.clientX-rect.left-rect.width/2;
    const y=e.clientY-rect.top-rect.height/2;
    btn.style.transform=`translate(${x*0.2}px,${y*0.2}px) scale(1.04)`;
  });
  btn.addEventListener('mouseleave',()=>{
    btn.style.transform='';
  });
});

// ── RIPPLE ─────────────────────────────────────────────────
function rippleClick(e){
  const target=e.currentTarget;
  const rect=target.getBoundingClientRect();
  const ripple=document.createElement('span');
  ripple.className='ripple';
  const size=Math.max(rect.width,rect.height);
  ripple.style.width=ripple.style.height=`${size}px`;
  ripple.style.left=`${e.clientX-rect.left-size/2}px`;
  ripple.style.top=`${e.clientY-rect.top-size/2}px`;
  target.appendChild(ripple);
  setTimeout(()=>ripple.remove(),600);
}
window.rippleClick=rippleClick;

// ── TILT ───────────────────────────────────────────────────
document.querySelectorAll('[data-tilt]').forEach(card=>{
  card.addEventListener('mousemove',(e)=>{
    const rect=card.getBoundingClientRect();
    const x=(e.clientX-rect.left)/rect.width-0.5;
    const y=(e.clientY-rect.top)/rect.height-0.5;
    card.style.transform=`translateY(-4px) perspective(800px) rotateY(${x*6}deg) rotateX(${-y*6}deg)`;
  });
  card.addEventListener('mouseleave',()=>{
    card.style.transform='';
  });
});

// ── PRIORITY EXPAND ────────────────────────────────────────
function togglePriority(){
  document.getElementById('priorityCard').classList.toggle('expanded');
}
window.togglePriority=togglePriority;

// ── NOTIFICATION PANEL ─────────────────────────────────────
function toggleNotif(){
  document.getElementById('notifPanel').classList.toggle('active');
}
window.toggleNotif=toggleNotif;
document.getElementById('notifBtn').addEventListener('click',toggleNotif);

// ── DOCK ───────────────────────────────────────────────────
const dockItems=document.querySelectorAll('.dock__item');
const dockPill=document.getElementById('dockPill');
function moveDockPill(item){
  const rect=item.getBoundingClientRect();
  const dockRect=item.closest('.dock').getBoundingClientRect();
  dockPill.style.opacity='1';
  dockPill.style.width=`${rect.width}px`;
  dockPill.style.height=`${rect.height}px`;
  dockPill.style.left=`${rect.left-dockRect.left}px`;
  dockPill.style.top=`${rect.top-dockRect.top}px`;
}
dockItems.forEach(item=>{
  item.addEventListener('mouseenter',()=>moveDockPill(item));
  item.addEventListener('click',()=>{
    dockItems.forEach(i=>i.classList.remove('dock__item--active'));
    item.classList.add('dock__item--active');
  });
});
document.getElementById('dock').addEventListener('mouseleave',()=>{
  const active=document.querySelector('.dock__item--active');
  if(active)moveDockPill(active);
});

// ── SEARCH ─────────────────────────────────────────────────
const searchInput=document.getElementById('searchInput');
const searchResults=document.getElementById('searchResults');
const searchData=[
  {icon:'👥',text:'Class 8B — Students',type:'Class'},
  {icon:'💰',text:'Fee Reports',type:'Report'},
  {icon:'📊',text:'Attendance Overview',type:'Report'},
  {icon:'👤',text:'Arjun Sharma',type:'Student'},
  {icon:'👤',text:'Priya Reddy',type:'Student'},
  {icon:'📢',text:'Send Notice',type:'Action'},
  {icon:'🚌',text:'Transport Routes',type:'Transport'},
  {icon:'📅',text:'Annual Sports Day',type:'Event'},
];
searchInput.addEventListener('focus',()=>{
  searchResults.classList.add('active');
  renderSearch('');
});
searchInput.addEventListener('blur',()=>{
  setTimeout(()=>searchResults.classList.remove('active'),200);
});
searchInput.addEventListener('input',(e)=>{
  renderSearch(e.target.value);
});
function renderSearch(q){
  const filtered=q?searchData.filter(d=>d.text.toLowerCase().includes(q.toLowerCase())):searchData.slice(0,5);
  searchResults.innerHTML=filtered.map(d=>
    `<div class="search-result"><span class="search-result__icon">${d.icon}</span><span class="search-result__text">${d.text}</span><span class="search-result__type">${d.type}</span></div>`
  ).join('')||'<div class="search-result"><span class="search-result__text">No results found</span></div>';
}

// ── SCROLL REVEAL ──────────────────────────────────────────
const observer=new IntersectionObserver((entries)=>{
  entries.forEach(entry=>{
    if(entry.isIntersecting){
      entry.target.classList.add('visible');
    }
  });
},{threshold:0.1});
document.querySelectorAll('.bento-card, .priority, .health-bars').forEach(el=>{
  el.classList.add('reveal');
  observer.observe(el);
});

// ── AI CONFIDENCE BAR ──────────────────────────────────────
setTimeout(()=>{
  const conf=document.getElementById('aiConfidence');
  if(conf)conf.style.width='96%';
},800);

// ── HEALTH BAR FILLS ───────────────────────────────────────
const barObserver=new IntersectionObserver((entries)=>{
  entries.forEach(entry=>{
    if(entry.isIntersecting){
      const fill=entry.target;
      fill.style.width=fill.dataset.fill+'%';
    }
  });
},{threshold:0.3});
document.querySelectorAll('.health-bar-fill').forEach(f=>barObserver.observe(f));

// ── INIT ANIMATIONS ────────────────────────────────────────
window.addEventListener('load',()=>{
  // Count-up all elements
  document.querySelectorAll('[data-countup]').forEach(el=>{
    countUp(el,parseInt(el.dataset.countup));
  });
  // Gauges
  animateGauge('gaugeAttendance',94);
  animateGauge('gaugeFees',87);
  // Health ring
  animateHealthRing();
  // Sparkline
  drawSparkline('sparkStudents',[220,225,230,235,240,238,242,245,248],'#6C63FF');
  // Dock pill initial
  const activeDock=document.querySelector('.dock__item--active');
  if(activeDock)setTimeout(()=>moveDockPill(activeDock),100);
});

// ── KEYBOARD SHORTCUT ──────────────────────────────────────
document.addEventListener('keydown',(e)=>{
  if((e.metaKey||e.ctrlKey)&&e.key==='k'){
    e.preventDefault();
    searchInput.focus();
  }
});
