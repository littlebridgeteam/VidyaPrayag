# AI Token Demand vs Free-Tier Capacity

> 20K daily active students | Current system + Agentic OS (25 features)

---

## System Demand

### Per Minute (peak, 10-hr school day)

| Metric | No Cache | With Cache (70% current / 30% agentic) |
|---|---|---|
| Requests/min | ~3,894 RPM | ~1,306 RPM |
| Tokens/min | ~1,402,783 TPM | ~446,882 TPM |

### Per Day

| Metric | No Cache | With Cache |
|---|---|---|
| Requests/day | ~233,609 | ~78,363 |
| Tokens/day | ~841.7M | ~268.1M |

### Per Month (30 days)

| Metric | No Cache | With Cache |
|---|---|---|
| Requests/month | ~7,008,270 | ~2,350,890 |
| Tokens/month | ~25.25B | ~8.04B |

### By Lane (with cache)

| Lane | % | TPM | TPD | Monthly |
|---|---|---|---|---|
| REASON | 88% | ~393K | ~236M | ~7.07B |
| CLASSIFY | 7% | ~31K | ~19M | ~563M |
| FAST_CHAT | 3% | ~13K | ~8M | ~241M |
| BATCH | 2% | ~9K | ~5M | ~161M |

### Top Consumers

| Feature | Calls/day | Tokens/day |
|---|---|---|
| Tutor Agent (current) | ~65,200 | ~205M |
| F8 Homework Auto-Checker | 9,800 | 22.1M |
| F7 Fee Recovery | 2,000 | 2.0M |
| F21 Query Router | 1,700 | 1.4M |
| Orchestrator AI conditions | 1,500 | 0.5M |
| F1 Attendance Reconciliation | 700 | 0.2M |

---

## Free-Tier Capacity

### Per Minute

| Provider | RPM | TPM |
|---|---|---|
| Cerebras | 30 | 60,000 |
| Groq | 30 | 6,000 |
| Groq_FAST | 30 | 6,000 |
| SambaNova | ~20 | Unknown |
| Mistral | 60 | 500,000 |
| OpenRouter | 20 | Provider-dep. |
| Gemini | 10 | 250,000 |
| NVIDIA_REASON | 40 | Unknown |
| NVIDIA_FAST | 40 | Unknown |
| **TOTAL** | **~280** | **~822,000** |

### Per Day

| Provider | RPD | TPD | Constraint |
|---|---|---|---|
| Cerebras | Unlimited | 1,000,000 | TPD cap |
| Groq | 1,000 | ~1,000,000 | RPD cap |
| Groq_FAST | 1,000 | ~1,000,000 | RPD cap |
| SambaNova | Unknown | Unknown | — |
| Mistral | Unlimited | ~33,333,333 | Monthly cap |
| OpenRouter (free) | 50 | ~50,000 | RPD cap |
| OpenRouter ($10) | 1,000 | ~1,000,000 | RPD cap |
| Gemini | 1,500 | ~1,500,000 | RPD cap |
| NVIDIA_REASON | ~1,000 | ~1,000,000 | Credit-based |
| NVIDIA_FAST | ~1,000 | ~1,000,000 | Credit-based |
| **TOTAL (free)** | **~5,550** | **~9.08M** | |
| **TOTAL ($10 OR)** | **~6,500** | **~10.03M** | |

### Per Month

| Provider | Monthly Tokens |
|---|---|
| Mistral | 1,000,000,000 |
| Gemini | 45,000,000 |
| Cerebras | 30,000,000 |
| Groq | 30,000,000 |
| Groq_FAST | 30,000,000 |
| OpenRouter ($10) | 30,000,000 |
| NVIDIA_REASON | 30,000,000 |
| NVIDIA_FAST | 30,000,000 |
| OpenRouter (free) | 1,500,000 |
| **TOTAL** | **~301M** |

---

## Gap Analysis

| Metric | Demand (cached) | Free Capacity | Gap |
|---|---|---|---|
| **TPM** | ~447K | ~822K | **OK** (1.8x headroom) |
| **TPD** | ~268M | ~10M | **27x deficit** |
| **RPD** | ~78,363 | ~6,500 | **12x deficit** |
| **Monthly tokens** | ~8.04B | ~301M | **27x deficit** |
| **Monthly requests** | ~2.35M | ~195K | **12x deficit** |

### Verdict

- **TPM (per-minute) is survivable** on free tiers at peak load
- **RPD (requests/day) is the primary bottleneck** — free tiers cover only ~8% of demand
- **Monthly token volume** — Mistral's 1B free cap covers ~12% alone, but total free only covers ~4%
- **Paid providers must absorb ~93% of request volume** and ~96% of token volume
