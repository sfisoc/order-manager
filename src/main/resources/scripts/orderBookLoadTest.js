import http from "k6/http";
import { check, sleep, group } from "k6";

var domain = "http://localhost:8890";

export const options = {
  stages: [
    { duration: "30s", target: 20 },
    { duration: "1m", target: 20 },
    { duration: "10s", target: 0 },
  ],
};

var headers = { "Content-Type": "application/json" };

export default function () {
  group("Order Book GETs", function () {
    let res = http.get("http://localhost:8890/v1/BTCZAR/tradehistory/10", {
      headers,
    });
    check(res, { "Trade history status is 200 ": (r) => r.status === 200 });
    sleep(1);

    res = http.get("http://localhost:8890/v1/BTCZAR/orderbook", { headers });
    check(res, { "Order Book status is 200": (r) => r.status === 200 });
    sleep(1);
  });

  group("Order Book POSTs", function () {
    const buyPayLoad = JSON.stringify({
      side: "BUY",
      price: "789.89",
      quantity: "500",
      timeInForce: "GTC",
    });

    let buyRes = http.post(
      "http://localhost:8890/v1/BTCZAR/orderbook/limit",
      buyPayLoad,
      { headers },
    );

    check(buyRes, { "Buy payload status is 200": (r) => r.status === 200 });
    sleep(1);

    const sellPayLoad = JSON.stringify({
      side: "SELL",
      price: "780.80",
      quantity: "400",
      timeInForce: "GTC",
    });

    let sellRes = http.post(
      "http://localhost:8890/v1/BTCZAR/orderbook/limit",
      sellPayLoad,
      { headers },
    );

    check(sellRes, { "Sell payload status is 200": (r) => r.status === 200 });
    sleep(1);
  });
}
