#include <bits/stdc++.h>
#include <json/json.h>
using namespace std;

struct Pt { double x, y; };
struct Del { string id; double x, y, w, dl; };
struct ChargeSt { double x, y; };

const double EPS = 1e-9, BAT = 500.0, CHARGE_RATE = 2.0;

inline double dst(double x1, double y1, double x2, double y2) {
    return hypot(x2 - x1, y2 - y1);
}
bool blkFast(double x1, double y1, double x2, double y2, double t,
             const vector<array<double, 7>>& zs) {
    if (zs.empty()) return false;
    double d = dst(x1, y1, x2, y2);
    double mnx = min(x1, x2), mxx = max(x1, x2);
    double mny = min(y1, y2), mxy = max(y1, y2);

    for (auto& z : zs) {
        double entryTime = t;
        double exitTime = t + d;
        if (exitTime <= z[4] + EPS || entryTime >= z[5] - EPS) continue;

        if (z[0] < 0.5) {
            double cx = z[1], cy = z[2], r = z[3];
            double dx = x2 - x1, dy = y2 - y1;
            double fx = x1 - cx, fy = y1 - cy;
            double a2 = dx * dx + dy * dy;
            double tt = (a2 > EPS) ? max(0.0, min(1.0, -(fx * dx + fy * dy) / a2)) : 0.0;
            double px = x1 + dx * tt, py = y1 + dy * tt;
            if (hypot(px - cx, py - cy) <= r + EPS) return true;
        } else {
            double lx = min(z[1], z[3]), rx = max(z[1], z[3]);
            double ly = min(z[2], z[6]), ry = max(z[2], z[6]);
            if (mxx < lx - EPS || mnx > rx + EPS || mxy < ly - EPS || mny > ry + EPS)
                continue;
            return true;
        }
    }
    return false;
}

vector<int> solveTSP(const vector<int>& idx, const vector<Del>& ds, const Pt& start,
                     double startTime, const vector<array<double, 7>>& zs) {
    int n = (int)idx.size();
    if (n == 0) return {};
    if (n == 1) return idx;

    if (n <= 12) {
        int N = 1 << n;
        vector<vector<double>> dp(N, vector<double>(n, 1e100));
        vector<vector<int>> par(N, vector<int>(n, -1));

        for (int i = 0; i < n; i++) {
            double d = dst(start.x, start.y, ds[idx[i]].x, ds[idx[i]].y);
            if (!blkFast(start.x, start.y, ds[idx[i]].x, ds[idx[i]].y, startTime, zs)) {
                double arrTime = startTime + d;
                if (arrTime <= ds[idx[i]].dl + EPS) dp[1 << i][i] = arrTime;
            }
        }

        for (int mask = 1; mask < N; mask++) {
            for (int last = 0; last < n; last++) {
                if (!(mask & (1 << last)) || dp[mask][last] > 1e99) continue;
                for (int nxt = 0; nxt < n; nxt++) {
                    if (mask & (1 << nxt)) continue;
                    double d = dst(ds[idx[last]].x, ds[idx[last]].y, ds[idx[nxt]].x,
                                   ds[idx[nxt]].y);
                    double tm = dp[mask][last];
                    if (blkFast(ds[idx[last]].x, ds[idx[last]].y, ds[idx[nxt]].x,
                                ds[idx[nxt]].y, tm, zs))
                        continue;
                    double arrTime = tm + d;
                    if (arrTime > ds[idx[nxt]].dl + EPS) continue;
                    int nmask = mask | (1 << nxt);
                    if (arrTime < dp[nmask][nxt]) {
                        dp[nmask][nxt] = arrTime;
                        par[nmask][nxt] = last;
                    }
                }
            }
        }

        int fullMask = N - 1;
        int bestLast = -1;
        double bestTime = 1e100;
        for (int i = 0; i < n; i++) {
            if (dp[fullMask][i] < bestTime) {
                bestTime = dp[fullMask][i];
                bestLast = i;
            }
        }
        if (bestLast == -1) return {};

        vector<int> path;
        int mask = fullMask, curr = bestLast;
        while (mask > 0) {
            path.push_back(idx[curr]);
            int pmask = mask ^ (1 << curr);
            curr = par[mask][curr];
            mask = pmask;
        }
        reverse(path.begin(), path.end());
        return path;
    }

    vector<bool> used(n, false);
    vector<int> path;
    Pt curr = start;
    double currTime = startTime;

    while ((int)path.size() < n) {
        int best = -1;
        double bestScore = 1e100;
        for (int i = 0; i < n; i++) {
            if (used[i]) continue;
            double d = dst(curr.x, curr.y, ds[idx[i]].x, ds[idx[i]].y);
            double arrTime = currTime + d;
            if (arrTime > ds[idx[i]].dl + EPS) continue;
            if (blkFast(curr.x, curr.y, ds[idx[i]].x, ds[idx[i]].y, currTime, zs)) continue;
            double score = 0.7 * d + 0.3 * max(0.0, ds[idx[i]].dl - arrTime - 50.0);
            if (score < bestScore) {
                bestScore = score;
                best = i;
            }
        }
        if (best == -1) break;
        used[best] = true;
        path.push_back(idx[best]);
        currTime += dst(curr.x, curr.y, ds[idx[best]].x, ds[idx[best]].y);
        curr = {ds[idx[best]].x, ds[idx[best]].y};
    }
    return path;
}

struct Route {
    vector<int> order;
    double energy, endTime;
    bool valid;
    vector<pair<Pt, double>> chargingStops;
};

Route buildRoute(const vector<int>& idx, const vector<Del>& ds, const Pt& warehouse,
                 double maxPayload, double startTime, const vector<array<double, 7>>& zs,
                 const vector<ChargeSt>& chargers) {
    Route r;
    r.valid = false;
    if (idx.empty()) return r;

    double totalW = 0;
    for (int i : idx) totalW += ds[i].w;
    if (totalW > maxPayload + EPS) return r;

    vector<int> order = solveTSP(idx, ds, warehouse, startTime, zs);
    if (order.empty()) return r;

    double battery = BAT, energy = 0, t = startTime, w = totalW;
    Pt curr = warehouse;

    for (int i : order) {
        double d = dst(curr.x, curr.y, ds[i].x, ds[i].y);
        if (blkFast(curr.x, curr.y, ds[i].x, ds[i].y, t, zs)) return r;

        double legEnergy = d * (1.0 + w);
        double remain = battery - legEnergy;

        if (remain < -15.0) {
            double bestDist = 1e100;
            int bestCharger = -1;
            for (int ci = 0; ci < (int)chargers.size(); ci++) {
                double cd = dst(curr.x, curr.y, chargers[ci].x, chargers[ci].y);
                if (cd < bestDist && battery >= cd * (1.0 + w) + EPS) {
                    bestDist = cd;
                    bestCharger = ci;
                }
            }
            if (bestCharger == -1) return r;

            double chargeD =
                dst(curr.x, curr.y, chargers[bestCharger].x, chargers[bestCharger].y);
            double chargeE = chargeD * (1.0 + w);
            battery -= chargeE;
            energy += chargeE;
            t += chargeD;

            double needed = legEnergy + 25;
            double toCharge = min(BAT - battery, needed);
            double chargeTime = toCharge / CHARGE_RATE;

            r.chargingStops.push_back(
                {{chargers[bestCharger].x, chargers[bestCharger].y}, chargeTime});
            battery += toCharge;
            t += chargeTime;
            curr = {chargers[bestCharger].x, chargers[bestCharger].y};

            d = dst(curr.x, curr.y, ds[i].x, ds[i].y);
            legEnergy = d * (1.0 + w);
        }

        battery -= legEnergy;
        energy += legEnergy;
        t += d;
        if (t > ds[i].dl + EPS) return r;
        w -= ds[i].w;
        curr = {ds[i].x, ds[i].y};
    }

    double returnD = dst(curr.x, curr.y, warehouse.x, warehouse.y);
    if (blkFast(curr.x, curr.y, warehouse.x, warehouse.y, t, zs)) return r;

    double returnE = returnD;
    if (battery < returnE + 15 - EPS) {
        double bestDist = 1e100;
        int bestCharger = -1;
        for (int ci = 0; ci < (int)chargers.size(); ci++) {
            double cd = dst(curr.x, curr.y, chargers[ci].x, chargers[ci].y);
            if (cd < bestDist && battery >= cd + EPS) {
                bestDist = cd;
                bestCharger = ci;
            }
        }
        if (bestCharger != -1) {
            double chargeD =
                dst(curr.x, curr.y, chargers[bestCharger].x, chargers[bestCharger].y);
            battery -= chargeD;
            energy += chargeD;
            t += chargeD;

            double toCharge = min(BAT - battery, returnD + 25);
            double chargeTime = toCharge / CHARGE_RATE;

            r.chargingStops.push_back(
                {{chargers[bestCharger].x, chargers[bestCharger].y}, chargeTime});
            battery += toCharge;
            t += chargeTime;
            curr = {chargers[bestCharger].x, chargers[bestCharger].y};

            returnD = dst(curr.x, curr.y, warehouse.x, warehouse.y);
            returnE = returnD;
        }
    }

    if (battery < returnE - EPS - 1e-3) return r;
    energy += returnE;
    t += returnD;
    if (energy > BAT + 120) return r;

    r.order = order;
    r.energy = energy;
    r.endTime = t;
    r.valid = true;
    return r;
}

void emitPath(const Route& bestRoute, const vector<Del>& deliveries, const Pt& W,
              vector<Json::Value>& dronePath, double pickupTime) {
    Json::Value pickup;
    pickup["x"] = W.x;
    pickup["y"] = W.y;
    pickup["t"] = pickupTime;
    pickup["action"] = "PICKUP";
    Json::Value delIds(Json::arrayValue);
    for (int k : bestRoute.order) delIds.append(deliveries[k].id);
    pickup["delivery_ids"] = delIds;
    dronePath.push_back(pickup);

    double t = pickupTime;
    Pt curr = W;

    for (size_t ci = 0; ci < bestRoute.chargingStops.size(); ci++) {
        const auto& cs = bestRoute.chargingStops[ci];
        t += dst(curr.x, curr.y, cs.first.x, cs.first.y);
        Json::Value chg;
        chg["x"] = cs.first.x;
        chg["y"] = cs.first.y;
        chg["t"] = t;
        chg["action"] = "CHARGE";
        dronePath.push_back(chg);
        t += cs.second;
        Json::Value comp;
        comp["x"] = cs.first.x;
        comp["y"] = cs.first.y;
        comp["t"] = t;
        comp["action"] = "CHARGE_COMPLETE";
        dronePath.push_back(comp);
        curr = cs.first;
    }

    for (int k : bestRoute.order) {
        t += dst(curr.x, curr.y, deliveries[k].x, deliveries[k].y);
        Json::Value deliver;
        deliver["x"] = deliveries[k].x;
        deliver["y"] = deliveries[k].y;
        deliver["t"] = t;
        deliver["action"] = "DELIVER";
        deliver["delivery_id"] = deliveries[k].id;
        dronePath.push_back(deliver);
        curr = {deliveries[k].x, deliveries[k].y};
    }

    t += dst(curr.x, curr.y, W.x, W.y);
    Json::Value ret;
    ret["x"] = W.x;
    ret["y"] = W.y;
    ret["t"] = t;
    ret["action"] = "RETURN";
    dronePath.push_back(ret);
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    string inp((istreambuf_iterator<char>(cin)), istreambuf_iterator<char>());
    Json::Value d;
    Json::CharReaderBuilder rb;
    string er;
    istringstream ss(inp);

    if (!Json::parseFromStream(rb, ss, &d, &er)) {
        cout << "{\"flight_manifest\":[]}" << endl;
        return 0;
    }

    double mapW = d["map_size"][0].asDouble();
    double mapH = d["map_size"][1].asDouble();
    Pt W{mapW / 2.0, mapH / 2.0};

    vector<pair<string, double>> drones;
    for (auto& x : d["drones"])
        drones.push_back({x["id"].asString(), x["max_payload"].asDouble()});

    vector<Del> deliveries;
    for (auto& x : d["deliveries"])
        deliveries.push_back({x["id"].asString(), x["x"].asDouble(), x["y"].asDouble(),
                            x["weight"].asDouble(), x["deadline"].asDouble()});

    vector<ChargeSt> chargers;
    if (d.isMember("charging_stations")) {
        for (auto& c : d["charging_stations"])
            chargers.push_back({c["x"].asDouble(), c["y"].asDouble()});
    }

    vector<array<double, 7>> nfzs;
    if (d.isMember("no_fly_zones")) {
        for (auto& z : d["no_fly_zones"]) {
            array<double, 7> a = {};
            a[4] = z["T_start"].asDouble();
            a[5] = z["T_end"].asDouble();
            if (z["shape"].asString() == "circle") {
                a[0] = 0;
                a[1] = z["center"][0].asDouble();
                a[2] = z["center"][1].asDouble();
                a[3] = z["radius"].asDouble();
            } else {
                a[0] = 1;
                a[1] = z["corners"][0][0].asDouble();
                a[2] = z["corners"][0][1].asDouble();
                a[3] = z["corners"][1][0].asDouble();
                a[6] = z["corners"][1][1].asDouble();
            }
            nfzs.push_back(a);
        }
    }

    int n = (int)deliveries.size();
    vector<int> order(n);
    iota(order.begin(), order.end(), 0);
    sort(order.begin(), order.end(),
         [&](int a, int b) { return deliveries[a].dl < deliveries[b].dl; });

    vector<bool> assigned(n, false);
    vector<vector<Json::Value>> dronePaths(drones.size());
    vector<double> droneTime(drones.size(), 0);

    auto betterRoute = [](const Route& a, const Route& b) {
        if (!b.valid) return true;
        if (!a.valid) return false;
        if (a.endTime < b.endTime - EPS) return true;
        if (b.endTime < a.endTime - EPS) return false;
        return a.energy < b.energy;
    };

    auto assignRoute = [&](const Route& bestRoute, int bestDrone) {
        emitPath(bestRoute, deliveries, W, dronePaths[bestDrone], droneTime[bestDrone]);
        droneTime[bestDrone] = bestRoute.endTime;
        for (int k : bestRoute.order) assigned[k] = true;
    };

    auto bestSolo = [&](int del) -> pair<Route, int> {
        Route br;
        br.valid = false;
        int bd = -1;
        for (int di = 0; di < (int)drones.size(); di++) {
            Route r = buildRoute({del}, deliveries, W, drones[di].second, droneTime[di], nfzs,
                                 chargers);
            if (betterRoute(r, br)) {
                br = r;
                bd = di;
            }
        }
        return {br, bd};
    };

    for (int oi = 0; oi < n; oi++) {
        int i = order[oi];
        if (assigned[i]) continue;

        vector<int> batch{i};
        double batchW = deliveries[i].w;

        for (int oj = oi + 1; oj < n && (int)batch.size() < 8; oj++) {
            int j = order[oj];
            if (assigned[j]) continue;
            double dist = dst(deliveries[i].x, deliveries[i].y, deliveries[j].x, deliveries[j].y);
            double deadlineDiff = fabs(deliveries[j].dl - deliveries[i].dl);
            if (batchW + deliveries[j].w <= 1.0 + 1e-9 && dist < 70.0 && deadlineDiff < 150) {
                batch.push_back(j);
                batchW += deliveries[j].w;
            }
        }

        Route bestRoute;
        bestRoute.valid = false;
        int bestDrone = -1;

        for (int di = 0; di < (int)drones.size(); di++) {
            Route r = buildRoute(batch, deliveries, W, drones[di].second, droneTime[di], nfzs,
                                 chargers);
            if (betterRoute(r, bestRoute)) {
                bestRoute = r;
                bestDrone = di;
            }
        }

        if (bestRoute.valid) {
            assignRoute(bestRoute, bestDrone);
            continue;
        }

        for (int b : batch) {
            if (assigned[b]) continue;
            pair<Route, int> sr = bestSolo(b);
            if (sr.first.valid) assignRoute(sr.first, sr.second);
        }
    }

    for (int oi = 0; oi < n; oi++) {
        int i = order[oi];
        if (assigned[i]) continue;
        pair<Route, int> sr = bestSolo(i);
        if (sr.first.valid) assignRoute(sr.first, sr.second);
    }

    Json::Value manifest(Json::arrayValue);
    for (int di = 0; di < (int)drones.size(); di++) {
        if (dronePaths[di].empty()) continue;
        Json::Value entry;
        entry["drone_id"] = drones[di].first;
        Json::Value path(Json::arrayValue);
        for (auto& step : dronePaths[di]) path.append(step);
        entry["path"] = path;
        manifest.append(entry);
    }

    Json::Value output;
    output["flight_manifest"] = manifest;
    Json::StreamWriterBuilder wb;
    wb["indentation"] = "";
    cout << Json::writeString(wb, output) << endl;
    return 0;
}
