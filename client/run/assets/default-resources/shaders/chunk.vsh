#version 330

in vec3 v_pos;

uniform u_combined;

void main() {
    gl_Position = u_combined * vec4(v_pos, 1.0);
}