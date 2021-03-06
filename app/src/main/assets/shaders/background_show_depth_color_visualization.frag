#version 300 es
/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
precision mediump float;

// This shader pair shows the depth estimation instead of the camera image as
// the background. This behavior is mostly only useful as a demonstration of the
// depth feature.

uniform sampler2D u_CameraDepthTexture;

in vec2 v_CameraTexCoord;

layout(location = 0) out vec4 o_FragColor;

float Depth_GetCameraDepthInMillimeters(const sampler2D depthTexture,
                                        const vec2 depthUv) {
  // Depth is packed into the red and green components of its texture.
  // The texture is a normalized format, storing millimeters.
  vec3 packedDepthAndVisibility = texture(depthTexture, depthUv).xyz;
  return dot(packedDepthAndVisibility.xy, vec2(255.0, 256.0 * 255.0));
}

// Returns a color corresponding to the depth passed in. Colors range from red
// to green to blue, where red is closest and blue is farthest.
//
// Uses Turbo color mapping:
// https://ai.googleblog.com/2019/08/turbo-improved-rainbow-colormap-for.html
vec3 Depth_GetColorVisualization(float x) {
  const vec4 kRedVec4 = vec4(0.55305649, 3.00913185, -5.46192616, -11.11819092);
  const vec4 kGreenVec4 =
      vec4(0.16207513, 0.17712472, 15.24091500, -36.50657960);
  const vec4 kBlueVec4 =
      vec4(-0.05195877, 5.18000081, -30.94853351, 81.96403246);
  const vec2 kRedVec2 = vec2(27.81927491, -14.87899417);
  const vec2 kGreenVec2 = vec2(25.95549545, -5.02738237);
  const vec2 kBlueVec2 = vec2(-86.53476570, 30.23299484);
  const float kInvalidDepthThreshold = 0.01;

  // Adjusts color space via 6 degree poly interpolation to avoid pure red.
  x = clamp(x * 0.9 + 0.03, 0.0, 1.0);
  vec4 v4 = vec4(1.0, x, x * x, x * x * x);
  vec2 v2 = v4.zw * v4.z;
  vec3 polynomialColor = vec3(dot(v4, kRedVec4) + dot(v2, kRedVec2),
                              dot(v4, kGreenVec4) + dot(v2, kGreenVec2),
                              dot(v4, kBlueVec4) + dot(v2, kBlueVec2));

  return step(kInvalidDepthThreshold, x) * polynomialColor;
}

void main() {
  const highp float kMaxDepth = 8000.0;  // In millimeters.
  highp float depth =
      Depth_GetCameraDepthInMillimeters(u_CameraDepthTexture, v_CameraTexCoord);
  highp float normalizedDepth = clamp(depth / kMaxDepth, 0.0, 1.0);
  o_FragColor = vec4(Depth_GetColorVisualization(normalizedDepth), 1.0);
}
