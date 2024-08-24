import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/login/theme/lumo/vaadin-login-overlay.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/color-global.js';
import '@vaadin/vaadin-lumo-styles/typography-global.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '380c4a70a1d6a1d1eb01b0ce315c58f7769d97a92c556e40d63f937a137ef90d') {
    pending.push(import('./chunks/chunk-1ef702e895fdf4ad70d68a304899022be3636e39090618bc6b4313f8100d16c0.js'));
  }
  if (key === 'd450294c8d7d650ddd3dc865fb9be155d9d03d4eb54f119a06e55fc6f3553820') {
    pending.push(import('./chunks/chunk-99ca4235193e5ee8f4f46c6ccc5e8928152f8c02774c7e09ca02598c56b938a3.js'));
  }
  if (key === '69ddbc4483dae0729753c5b918ca6db5c9628285e7e64f77934b3f2111c1871e') {
    pending.push(import('./chunks/chunk-1ef702e895fdf4ad70d68a304899022be3636e39090618bc6b4313f8100d16c0.js'));
  }
  if (key === 'd95a2990b1c3753e09714d3193ab51996ae12b1fca5ef392043d9008e6c235f2') {
    pending.push(import('./chunks/chunk-99ca4235193e5ee8f4f46c6ccc5e8928152f8c02774c7e09ca02598c56b938a3.js'));
  }
  if (key === '6b7bdea818983bcd3345f6d87a0c3013f3f107f566fe43a29521185d25738943') {
    pending.push(import('./chunks/chunk-110eea8dbbeb2f3da2429691c3d1393a8de64c9e4f3e39d237374a69b49fede4.js'));
  }
  if (key === '2205f562e5a162ffef64bb2a23edf42490432ddf160e7a976f415612ea1c8ebb') {
    pending.push(import('./chunks/chunk-99ca4235193e5ee8f4f46c6ccc5e8928152f8c02774c7e09ca02598c56b938a3.js'));
  }
  if (key === '7293f51892948f2c1fec15ccdc761c300b1b86cd95d15b8bfac743e8b0f89511') {
    pending.push(import('./chunks/chunk-99ca4235193e5ee8f4f46c6ccc5e8928152f8c02774c7e09ca02598c56b938a3.js'));
  }
  if (key === '81e9f9cb3bf725c12106f7dc4af8f5f76f4c10b76ecc8562276ae5e8d0735b42') {
    pending.push(import('./chunks/chunk-1ef702e895fdf4ad70d68a304899022be3636e39090618bc6b4313f8100d16c0.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}